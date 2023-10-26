package com.example.demo.fhir;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.http.ProtocolException;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

import static java.util.Collections.singletonList;


@Component
public class FhirRoute extends RouteBuilder {
    @Override
    public void configure() {
        from("file:{{input}}").routeId("fhir-example")
                .onException(Throwable.class)
                    .handled(true)
                    .log(LoggingLevel.ERROR, "Error connecting to FHIR server with URL:{{serverUrl}}, please check the application.properties file ${exception.message}")
                    .end()
                .onException(ProtocolException.class)
                    .handled(true)
                    .log(LoggingLevel.ERROR, "Error connecting to FHIR server with URL:{{serverUrl}}, please check the application.properties file ${exception.message}")
                    .end()
                .onException(HL7Exception.class)
                    .handled(true)
                    .log(LoggingLevel.ERROR, "Error unmarshalling ${file:name} ${exception.message}")
                    .end()
                .log("Converting ${file:name}")
                .unmarshal().fhirXml()
                .process(this::enrich)
                .marshal().fhirJson()
                .convertBodyTo(String.class)
                .log("Sending Patient: ${body}")
                .to("fhir://create/resource?inBody=resourceAsString&serverUrl={{serverUrl}}&fhirVersion={{fhirVersion}}")
                .to("direct:result");

        from("direct:result")
                .log("Patient created successfully: ${body}")
                .end();
    }

    private void enrich(Exchange exch) {
        Patient patient = exch.getIn().getBody(Patient.class);
        HumanName childName = (HumanName)patient
                .addChild("name");
        childName.setGiven(singletonList(new StringType("Dmitri M")));
        childName.setFamily("Guha");
    }
}
