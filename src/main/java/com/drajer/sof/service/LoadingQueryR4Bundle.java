package com.drajer.sof.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.drajer.eca.model.ActionRepo;
import com.drajer.ecrapp.util.ApplicationUtils;
import com.drajer.sof.model.LaunchDetails;
import com.drajer.sof.model.R4FhirData;
import com.drajer.sof.utils.FhirContextInitializer;
import com.drajer.sof.utils.R4ResourcesData;
import java.util.Date;
import java.util.List;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoadingQueryR4Bundle {

  @Autowired FhirContextInitializer fhirContextInitializer;

  @Autowired R4ResourcesData r4ResourcesData;

  private final Logger logger = LoggerFactory.getLogger(LoadingQueryR4Bundle.class);

  public Bundle createR4Bundle(
      LaunchDetails launchDetails, R4FhirData r4FhirData, Date start, Date end) {
    logger.info("Initializing FHIR Context for Version::::" + launchDetails.getFhirVersion());
    FhirContext context = fhirContextInitializer.getFhirContext(launchDetails.getFhirVersion());
    logger.info("Initializing Client");
    IGenericClient client =
        fhirContextInitializer.createClient(
            context, launchDetails.getEhrServerURL(), launchDetails.getAccessToken());

    Bundle bundle =
        r4ResourcesData.getCommonResources(r4FhirData, start, end, launchDetails, client, context);

    Encounter encounter =
        (Encounter) r4ResourcesData.getResourceFromBundle(bundle, Encounter.class);

    // Get Pregnancy Observations
    try {
      logger.info("Get Preganancy Observation Data");
      List<Observation> observationList =
          r4ResourcesData.getObservationData(
              context, client, launchDetails, r4FhirData, encounter, start, end);
      logger.info("Filtered Observations---->" + observationList.size());
      r4FhirData.setPregnancyObs(observationList);
      for (Observation observation : observationList) {
        BundleEntryComponent observationsEntry =
            new BundleEntryComponent().setResource(observation);
        bundle.addEntry(observationsEntry);
      }
    } catch (Exception e) {
      logger.error("Error in getting Preganancy Observation Data");
    }

    // Get Travel Observations
    try {
      logger.info("Get Travel Observation Data");
      List<Observation> observationList =
          r4ResourcesData.getObservationData(
              context, client, launchDetails, r4FhirData, encounter, start, end);
      logger.info("Filtered Observations---->" + observationList.size());
      r4FhirData.setTravelObs(observationList);
      for (Observation observation : observationList) {
        BundleEntryComponent observationsEntry =
            new BundleEntryComponent().setResource(observation);
        bundle.addEntry(observationsEntry);
      }
    } catch (Exception e) {
      logger.error("Error in getting Travel Observation Data");
    }

    try {
      logger.info("Get MedicationStatement Data");
      List<MedicationStatement> medStatementsList =
          r4ResourcesData.getMedicationStatementData(
              context, client, launchDetails, r4FhirData, encounter, start, end);
      logger.info("Filtered MedicationStatement----------->" + medStatementsList.size());
      r4FhirData.setMedications(medStatementsList);
      for (MedicationStatement medStatement : medStatementsList) {
        BundleEntryComponent medStatementEntry =
            new BundleEntryComponent().setResource(medStatement);
        bundle.addEntry(medStatementEntry);
      }
    } catch (Exception e) {
      logger.error("Error in getting the MedicationStatement Data");
    }

    // Get Immunizations for Patients and laboratory category (Write a method).
    // Filter the Immunizations based on encounter Reference if encounter is
    // present.
    // If encounter is not present, then filter based on times (Start and end, if
    // Immunizations time is between start and end times) -- Do this later.
    // Add to the bundle
    try {
      List<Immunization> immunizationsList =
          r4ResourcesData.getImmunizationData(
              context, client, launchDetails, r4FhirData, encounter, start, end);
      logger.info("Filtered Immunizations----------->" + immunizationsList.size());
      r4FhirData.setImmunizations(immunizationsList);
      for (Immunization immunization : immunizationsList) {
        BundleEntryComponent immunizationEntry =
            new BundleEntryComponent().setResource(immunization);
        bundle.addEntry(immunizationEntry);
      }
    } catch (Exception e) {
      logger.error("Error in getting the Immunization Data");
    }

    // Get Diagnostic Reports for Patients (Write a method).
    // Filter the Diagnostic Reports based on encounter Reference if encounter is
    // present.
    // If encounter is not present, then filter based on times (Start and end, if
    // diagnostic Reports time is between start and end times) -- Do this later.
    // Add to the bundle
    try {
      List<DiagnosticReport> diagnosticReportList =
          r4ResourcesData.getDiagnosticReportData(
              context, client, launchDetails, r4FhirData, encounter, start, end);
      logger.info("Filtered DiagnosticReports----------->" + diagnosticReportList.size());
      r4FhirData.setDiagReports(diagnosticReportList);
      for (DiagnosticReport diagnosticReport : diagnosticReportList) {
        BundleEntryComponent diagnosticReportEntry =
            new BundleEntryComponent().setResource(diagnosticReport);
        bundle.addEntry(diagnosticReportEntry);
      }
    } catch (Exception e) {
      logger.error("Error in getting the DiagnosticReport Data");
    }

    // Setting bundle to FHIR Data
    logger.info(
        "------------------------------CodeableConcept Codes------------------------------");
    logger.info("Encounter Codes Size=====>" + r4FhirData.getR4EncounterCodes().size());
    logger.info("Conditions Codes Size=====>" + r4FhirData.getR4ConditionCodes().size());
    logger.info("Observation Codes Size=====>" + r4FhirData.getR4LabResultCodes().size());
    logger.info("Medication Codes Size=====>" + r4FhirData.getR4MedicationCodes().size());
    logger.info("Immunization Codes Size=====>" + r4FhirData.getR4ImmunizationCodes().size());
    logger.info(
        "DiagnosticReport Codes Size=====>" + r4FhirData.getR4DiagnosticReportCodes().size());
    // logger.info("DiagnosticOrders Codes
    // Size=====>"+dstu2FhirData.getDiagnosticOrderCodes().size());

    // logger.info(context.newJsonParser().encodeResourceToString(bundle));
    String fileName =
        ActionRepo.getInstance().getLogFileDirectory()
            + "/LoadingQueryR4Bundle-"
            + launchDetails.getLaunchPatientId()
            + ".json";
    ApplicationUtils.saveDataToFile(
        context.newJsonParser().encodeResourceToString(bundle), fileName);
    return bundle;
  }
}
