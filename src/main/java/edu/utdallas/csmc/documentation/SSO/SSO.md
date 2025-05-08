# SSO Implementation

### Steps:

The OIT office already has requests from the CSMC about SSO
Here are the steps:
- [ ] Understand SAML and Single Sign-On authentication
- [ ] Configure the website to be a Service Provider
    - This is already halfway in the SSO branch of GitLab
        - Spring Boot SAML can be either implemented by xml bean file or by Java classes (the option we went with) but you can do XML if it's easier for you
    - Once configured, provide the technical information noted below about your SP you created:
        - SP Metadata. This can be provided in XML format or provide URL where the metadata file is publish. If possible, we highly recommend URL option.
        - Confirmation that the attributes required are: display name, common name, given name, surname. 
            - This is not sure about the name of the attributes 
- [ ] Test the Service Provider with OIT

---
### References:

- https://docs.spring.io/autorepo/docs/spring-security-saml/1.0.x-SNAPSHOT/reference/htmlsingle/
- https://github.com/vdenotaris/spring-boot-security-saml-sample
- https://github.com/cBioPortal/cbioportal
    - this one is for better understanding of how the business logic works with authentication
- I attached the 2 xml files in the same folder for reference
- Check out the SSO codes in the SSO branch, starting from SSOController.java
    - the progress of the code is to the point of figuring out what information we are getting back from the UTD IdP to pass onto the other controllers
    - the key may need to be regenerated on the server we are deploying
- Lastly, I have my email here in case you have any questions: minhduong.md25@gmail.com
