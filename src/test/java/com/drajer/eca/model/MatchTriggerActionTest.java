package com.drajer.eca.model;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.hl7.fhir.r4.model.Duration;
import org.hl7.fhir.r4.model.PlanDefinition.ActionRelationshipType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;

import com.drajer.eca.model.EventTypes.EcrActionTypes;
import com.drajer.eca.model.EventTypes.JobStatus;
import com.drajer.eca.model.EventTypes.WorkflowEvent;
import com.drajer.ecrapp.config.ValueSetSingleton;
import com.drajer.ecrapp.model.Eicr;
import com.drajer.ecrapp.service.WorkflowService;
import com.drajer.ecrapp.service.impl.EicrServiceImpl;
import com.drajer.ecrapp.util.ApplicationUtils;
import com.drajer.sof.model.Dstu2FhirData;
import com.drajer.sof.model.LaunchDetails;
import com.drajer.sof.model.R4FhirData;
import com.drajer.sof.service.LoadingQueryService;
import com.drajer.sof.service.TriggerQueryDstu2Bundle;
import com.drajer.sof.service.TriggerQueryService;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.dstu2.resource.Bundle;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ EcaUtils.class, ApplicationUtils.class, ActionRepo.class, WorkflowService.class })
public class MatchTriggerActionTest {

	private LaunchDetails mockDetails;
	private PatientExecutionState mockState;

	private WorkflowEvent launchType = WorkflowEvent.SCHEDULED_JOB;

	@InjectMocks
	private MatchTriggerAction matchTriggerAction;

	private Dstu2FhirData mockDstu2Data;
	private R4FhirData mockR4Data;

	private List<ActionData> codePaths;

	private ActionRepo mockActionRepo;
	private TriggerQueryService mockTrigQuerySrvc;

	TriggerQueryDstu2Bundle generateDstu2Bundle;

	@Before
	public void setUp() {

		mockDetails = PowerMockito.mock(LaunchDetails.class);
		mockState = PowerMockito.mock(PatientExecutionState.class);
		mockDstu2Data = PowerMockito.mock(Dstu2FhirData.class);
		mockR4Data = PowerMockito.mock(R4FhirData.class);

		mockTrigQuerySrvc = PowerMockito.mock(TriggerQueryService.class);

		if (mockActionRepo == null) {

			mockActionRepo = PowerMockito.mock(ActionRepo.class);
			;
		}

		PowerMockito.mockStatic(ActionRepo.class);
		PowerMockito.mockStatic(ApplicationUtils.class);
		PowerMockito.mockStatic(EcaUtils.class);

		PowerMockito.mockStatic(WorkflowService.class);

	}

	@Test
	public void test_Execute_GettingDataForTriggerQueryDstu2FhirData() throws Exception {

		MatchTriggerStatus matchTriggerStatus = new MatchTriggerStatus();
		matchTriggerStatus.setJobStatus(JobStatus.SCHEDULED);
		when(EcaUtils.getDetailStatus(mockDetails)).thenReturn(mockState);
		matchTriggerStatus = new MatchTriggerStatus();
		when(mockState.getMatchTriggerStatus()).thenReturn(matchTriggerStatus);
		when(ActionRepo.getInstance()).thenReturn(mockActionRepo);
		when(mockActionRepo.getTriggerQueryService()).thenReturn(mockTrigQuerySrvc);
		when(mockTrigQuerySrvc.getData(eq(mockDetails), eq(null), eq(null))).thenReturn(mockDstu2Data);

		ActionData actionData = new ActionData();
		actionData.setPath("mock test path");
		codePaths = new ArrayList<>();
		codePaths.add(actionData);
		matchTriggerAction.setTriggerData(codePaths);

		when(EcaUtils.matchTriggerCodesForDSTU2(codePaths, mockDstu2Data, mockState, mockDetails)).thenReturn(true);
		when(EcaUtils.getDetailStatus(mockDetails)).thenReturn(mockState);

		matchTriggerAction.execute(mockDetails, launchType);

		assertNotNull(mockDstu2Data);
		assertEquals(matchTriggerStatus.getJobStatus(), JobStatus.COMPLETED);

	}

	@Test
	public void test_Execute_GettingDataForTriggerQueryR4FhirData() throws Exception {
		MatchTriggerStatus matchTriggerStatus = new MatchTriggerStatus();
		matchTriggerStatus.setJobStatus(JobStatus.SCHEDULED);
		when(EcaUtils.getDetailStatus(mockDetails)).thenReturn(mockState);
		matchTriggerStatus = new MatchTriggerStatus();
		when(mockState.getMatchTriggerStatus()).thenReturn(matchTriggerStatus);
		when(ActionRepo.getInstance()).thenReturn(mockActionRepo);
		when(mockActionRepo.getTriggerQueryService()).thenReturn(mockTrigQuerySrvc);
		when(mockTrigQuerySrvc.getData(eq(mockDetails), eq(null), eq(null))).thenReturn(mockDstu2Data);
		ActionData actionData = new ActionData();
		actionData.setPath("mock test path");
		codePaths = new ArrayList<>();
		codePaths.add(actionData);
		matchTriggerAction.setTriggerData(codePaths);

		when(EcaUtils.matchTriggerCodesForR4(codePaths, mockR4Data, mockState, mockDetails)).thenReturn(true);
		when(EcaUtils.getDetailStatus(mockDetails)).thenReturn(mockState);

		matchTriggerAction.execute(mockDetails, launchType);

		assertNotNull(mockR4Data);
		assertEquals(matchTriggerStatus.getJobStatus(), JobStatus.COMPLETED);

	}

	@Test(expected = RuntimeException.class)
	public void test_Execute_WhenNoFhirData() {
		MatchTriggerStatus matchTriggerStatus = new MatchTriggerStatus();
		matchTriggerStatus.setJobStatus(JobStatus.SCHEDULED);
		when(EcaUtils.getDetailStatus(mockDetails)).thenReturn(mockState);
		matchTriggerStatus = new MatchTriggerStatus();
		when(mockState.getMatchTriggerStatus()).thenReturn(matchTriggerStatus);
		when(ActionRepo.getInstance()).thenReturn(mockActionRepo);
		when(mockActionRepo.getTriggerQueryService()).thenReturn(mockTrigQuerySrvc);
		when(mockTrigQuerySrvc.getData(eq(mockDetails), eq(null), eq(null))).thenReturn(mockDstu2Data);
		ActionData actionData = new ActionData();
		actionData.setPath("mock test path");
		codePaths = new ArrayList<>();
		codePaths.add(actionData);

		matchTriggerAction.execute(mockDetails, launchType);
	}

	@Test(expected = RuntimeException.class)
	public void testExecute_DetailObjIsInvalid() throws Exception {

		// Test
		matchTriggerAction.execute(null, launchType);
	}

}
