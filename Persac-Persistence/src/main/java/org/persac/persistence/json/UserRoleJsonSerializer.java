package org.persac.persistence.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.persac.persistence.model.UserRole;

import java.io.IOException;

/**
 * Created by maks on 01.11.15.
 */
public class UserRoleJsonSerializer extends JsonSerializer<UserRole> {

    @Override
    public void serialize(UserRole userRole,
                          JsonGenerator generator,
                          SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

        generator.writeStartObject();
        generator.writeFieldName("id");
        generator.writeNumber(userRole.getId());
        generator.writeFieldName("role");
        generator.writeObject(userRole.getRole());
        generator.writeEndObject();

    }
}
