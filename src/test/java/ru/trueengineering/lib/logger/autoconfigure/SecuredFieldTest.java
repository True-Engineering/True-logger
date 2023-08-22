package ru.trueengineering.lib.logger.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import ru.trueengineering.lib.logger.autoconfigure.mapping.ObjectMapperConfigurer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

class SecuredFieldTest {

    private ObjectMapper objectMapper = ObjectMapperConfigurer.prepareObjectMapper(Optional.empty());

    @Test
    void securedFieldTest() throws IOException, JSONException {
        Resource resource = new DefaultResourceLoader().getResource("classpath:securedFieldsTest.json");
        String expected = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        String actual = objectMapper.writeValueAsString(new Dto());
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
    }

    public static class Dto {

        private String login = "user";

        @SecuredField
        private String password = "password";

        private Boolean sex = true;

        @SecuredField
        private Boolean isMarried = false;

        private Integer id = 11123;

        @SecuredField
        private Integer age = 23;

        private String nullField = null;

        @SecuredField
        private String securedNullField = null;

        private InnerDto objectField = new InnerDto();

        @SecuredField
        private InnerDto securedObjectField = new InnerDto();

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Boolean getSex() {
            return sex;
        }

        public void setSex(Boolean sex) {
            this.sex = sex;
        }

        public Boolean getIsMarried() {
            return isMarried;
        }

        public void setIsMarried(Boolean married) {
            isMarried = married;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getNullField() {
            return nullField;
        }

        public void setNullField(String nullField) {
            this.nullField = nullField;
        }

        public String getSecuredNullField() {
            return securedNullField;
        }

        public void setSecuredNullField(String securedNullField) {
            this.securedNullField = securedNullField;
        }

        public InnerDto getObjectField() {
            return objectField;
        }

        public void setObjectField(InnerDto objectField) {
            this.objectField = objectField;
        }

        public InnerDto getSecuredObjectField() {
            return securedObjectField;
        }

        public void setSecuredObjectField(InnerDto securedObjectField) {
            this.securedObjectField = securedObjectField;
        }
    }

    public static class InnerDto {

        private Integer height = 175;

        @SecuredField
        private String weight = "60kg";

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }
    }

}