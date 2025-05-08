package edu.utdallas.csmc.web.model.session;

import edu.utdallas.csmc.web.model.session.Request.RequestStatus;
import lombok.extern.log4j.Log4j2;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Log4j2
@Converter(autoApply = true)
public class RequestStatusEnumConverter implements AttributeConverter<RequestStatus, String>{
    @Override
    public String convertToDatabaseColumn(RequestStatus status){ return status.name().toLowerCase(); }

    @Override
    public RequestStatus convertToEntityAttribute(String data){ return RequestStatus.valueOf(data.toUpperCase()); }

}
