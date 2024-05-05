package br.com.fulandt.security.filter;

import br.com.fulandt.core.domain.entity.ApplicationUser;
import br.com.fulandt.core.domain.property.JwtConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfiguration jwtConfiguration;

    @Override
    @SneakyThrows
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("Attemp autth. . .");
        ApplicationUser applicationUser =  new ObjectMapper().readValue(request.getInputStream(), ApplicationUser.class);

        if(applicationUser == null){
            throw new UsernameNotFoundException(String.format(
                    "username ou password invalid"));
        }
            log.info("creating the authnetication object for the user '{}'",applicationUser.getUsername());

        UsernamePasswordAuthenticationToken uToken =  new UsernamePasswordAuthenticationToken(applicationUser.getUsername(),applicationUser.getPassword(), Collections.emptyList());
        uToken.setDetails(applicationUser);
        return authenticationManager.authenticate(uToken);
    }

    @SneakyThrows
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        log.info("Auth success, generation JWE token", authResult.getName());
        SignedJWT signedJWT = createSignedJWT(authResult);
        String encrypetdToken = encryptToken(signedJWT);

        log.info("token  generated sucessfully, adding in to the response header");
        response.addHeader("Acess-Control-Expose-Headers", "XSRF-TOKEN ," + jwtConfiguration.getHeader().getName());
        response.addHeader(jwtConfiguration.getHeader().getName(), jwtConfiguration.getHeader().getPrefix() + encrypetdToken);
    }

    @SneakyThrows
    private SignedJWT createSignedJWT(Authentication auth){
        log.info("Starting to create the signed JWT");

        ApplicationUser applicationUser = (ApplicationUser) auth.getPrincipal();
        //cleams
        JWTClaimsSet jwtClaimsSet =  createJWTClaimSet(auth,applicationUser);

        //keys
        KeyPair rsaKeys =  gereratedKeyPair();
        log.info("Building JWK from the RSA keys");
        JWK jwk = new RSAKey.Builder((RSAPublicKey) rsaKeys.getPublic()).keyID(UUID.randomUUID().toString()).build();

        //assinando json
       SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.ES256)
               .jwk(jwk)
               .type(JOSEObjectType.JWT)
               .build(), jwtClaimsSet);
       log.info("Signing the token with the private RSA Key");
      RSASSASigner signer =  new RSASSASigner(rsaKeys.getPrivate());
      signedJWT.sign(signer);

      log.info("serialized token '{}'", signedJWT.serialize());
      return signedJWT;
    }

    private JWTClaimsSet createJWTClaimSet(Authentication auth, ApplicationUser applicationUser){
        log.info("creating the jwtClaimSet object for '{}'",applicationUser);
        return new JWTClaimsSet.Builder()
                .subject(applicationUser.getUsername())
                .claim("authorities", auth.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())).issuer("http://fulandt.com")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + (jwtConfiguration.getExpiration() * 1000)))
                .build();

    }
    @SneakyThrows
    private KeyPair gereratedKeyPair(){
        log.info("generating RSA 2048 bits keys");

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.genKeyPair();
    }
    //criptografia

    private String encryptToken(SignedJWT signedJWT) throws JOSEException {
        log.info("Starting the encryptToken method");
       DirectEncrypter directEncrypter = new DirectEncrypter(jwtConfiguration.getPrivateKey().getBytes());
       JWEObject jweObject = new JWEObject(new JWEHeader.Builder(JWEAlgorithm.DIR,EncryptionMethod.A128CBC_HS256)
               .contentType("JWT")
               .build(),new Payload(signedJWT));

        log.info("Encrypting token with system's private ley");
        jweObject.encrypt(directEncrypter);

        log.info("token encrypted");
        return  jweObject.serialize();
    }
}
