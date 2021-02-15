/*
 * Copyright 2021 - 2022 Procura B.V.
 *
 * In licentie gegeven krachtens de EUPL, versie 1.2
 * U mag dit werk niet gebruiken, behalve onder de voorwaarden van de licentie.
 * U kunt een kopie van de licentie vinden op:
 *
 *   https://github.com/vrijBRP/vrijBRP/blob/master/LICENSE.md
 *
 * Deze bevat zowel de Nederlandse als de Engelse tekst
 *
 * Tenzij dit op grond van toepasselijk recht vereist is of schriftelijk
 * is overeengekomen, wordt software krachtens deze licentie verspreid
 * "zoals deze is", ZONDER ENIGE GARANTIES OF VOORWAARDEN, noch expliciet
 * noch impliciet.
 * Zie de licentie voor de specifieke bepalingen voor toestemmingen en
 * beperkingen op grond van de licentie.
 */

package nl.procura.burgerzaken.dossiers.api.admin.v1.resources;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.procura.burgerzaken.dossiers.api.admin.v1.base.AdminApiContactInformation;
import nl.procura.burgerzaken.dossiers.api.admin.v1.base.AdminApiMunicipality;
import nl.procura.burgerzaken.dossiers.api.admin.v1.dossier.AdminApiDossier;
import nl.procura.burgerzaken.dossiers.api.admin.v1.dossier.AdminApiDossierStatus;
import nl.procura.burgerzaken.dossiers.api.admin.v1.dossier.AdminApiReferenceId;
import nl.procura.burgerzaken.dossiers.api.admin.v1.dossier.AdminDossierType;
import nl.procura.burgerzaken.dossiers.api.admin.v1.relocations.base.*;
import nl.procura.burgerzaken.dossiers.api.admin.v1.relocations.inter.AdminApiInterMunicipalRelocation;
import nl.procura.burgerzaken.dossiers.api.admin.v1.relocations.inter.AdminApiInterMunicipalRelocationPerson;
import nl.procura.burgerzaken.dossiers.components.ApiAccess;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierStatus;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled("Admin API not important anymore since relocation are add directly to the Front-desk")
class AdminInterRelocationResourceV1Test {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ApiAccess apiAccess;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private EntityManager em;

  @BeforeEach
  public void setUp() {
    apiAccess.adminScope(mockMvc);
  }

  @AfterEach
  public void tearDown() {
    apiAccess.delete();
  }

  @Test
  void postMustCreateDossierInDb() throws Exception {
    String referenceId = "id";
    AdminApiInterMunicipalRelocation relocation = newRelocation(
        singleton(new AdminApiReferenceId(referenceId, "description")));
    // when
    AdminApiInterMunicipalRelocation response = createRelocation(relocation);
    String dossierId = response.getDossier().getDossierId();
    // then database must be updated correctly
    Tuple db = (Tuple) em.createNativeQuery("SELECT * FROM doss" +
        " WHERE casenr = ?", Tuple.class)
        .setParameter(1, dossierId)
        .getSingleResult();
    Object dossId = db.get("doss_id");
    List<Tuple> list = (List<Tuple>) em.createNativeQuery("SELECT * FROM doss_ref" +
        " WHERE doss_id = ?", Tuple.class)
        .setParameter(1, dossId)
        .getResultList();
    assertEquals(1, list.size());
    assertEquals(referenceId, list.get(0).get("ref_casenr"));
  }

  @Test
  void postWithDossierIdMustReturnBadRequest() throws Exception {
    // when
    AdminApiInterMunicipalRelocation relocation = newRelocation(
        singleton(new AdminApiReferenceId("id", "description")));
    relocation.getDossier().setDossierId("a-dossier-id");
    // when
    postRelocation(relocation)
        .andExpect(status().isBadRequest());
  }

  @Test
  void createdRelocationMustBeReturnedWithGet() throws Exception {
    // given
    AdminApiInterMunicipalRelocation relocation = newRelocation(
        singleton(new AdminApiReferenceId("id", "description")));
    String dossierId = createRelocation(relocation).getDossier().getDossierId();
    // when
    AdminApiInterMunicipalRelocation response = getRelocation(dossierId);
    // then
    assertEquals("created", response.getDossier().getStatus().getCode());
    assertEquals("8900", response.getPreviousMunicipality().getCode());
  }

  @Test
  void deletedRelocationMustNotBeInDb() throws Exception {
    // given
    AdminApiInterMunicipalRelocation relocation = newRelocation(
        singleton(new AdminApiReferenceId("id", "description")));
    String dossierId = createRelocation(relocation).getDossier().getDossierId();
    // when delete
    mockMvc.perform(delete("/admin/api/v1/dossiers/{dossierId}", dossierId)
        .headers(apiAccess.authorization())
        .accept(MediaType.APPLICATION_JSON))
        // then
        .andExpect(status().isOk())
        .andReturn();
    // then record must not exist in database
    List<Tuple> list = (List<Tuple>) em.createNativeQuery("SELECT * FROM doss" +
        " WHERE casenr = ?", Tuple.class)
        .setParameter(1, dossierId)
        .getResultList();
    assertEquals(0, list.size());
  }

  private AdminApiInterMunicipalRelocation createRelocation(AdminApiInterMunicipalRelocation relocation)
      throws Exception {
    MvcResult response = postRelocation(relocation)
        // then
        .andExpect(status().isCreated())
        .andReturn();
    return mapper.readValue(response.getResponse().getContentAsByteArray(), AdminApiInterMunicipalRelocation.class);
  }

  private ResultActions postRelocation(AdminApiInterMunicipalRelocation relocation) throws Exception {
    String requestJson = mapper.writeValueAsString(relocation);
    return mockMvc.perform(post("/admin/api/v1/relocations/inter")
        .headers(apiAccess.authorization())
        .content(requestJson)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
  }

  private AdminApiInterMunicipalRelocation getRelocation(String dossierId) throws Exception {
    MvcResult response = mockMvc.perform(get("/admin/api/v1/relocations/inter/{dossierId}", dossierId)
        .headers(apiAccess.authorization())
        .accept(MediaType.APPLICATION_JSON))
        // then
        .andExpect(status().isOk())
        .andReturn();
    return mapper.readValue(response.getResponse().getContentAsByteArray(), AdminApiInterMunicipalRelocation.class);
  }

  private static AdminApiInterMunicipalRelocation newRelocation(Set<AdminApiReferenceId> referenceIds) {
    AdminApiContactInformation contactInfo = AdminApiContactInformation.builder()
        .email("burgerzaken@procura.nl")
        .telephoneNumber("12345")
        .build();

    return AdminApiInterMunicipalRelocation.builder()
        .dossier(AdminApiDossier.builder()
            .referenceIds(referenceIds)
            .type(AdminDossierType.builder()
                .code("B1234")
                .description("Intergemeentelijke verhuizing").build())
            .status(AdminApiDossierStatus.of(DossierStatus.CREATED))
            .startDate(LocalDate.of(2019, Month.JANUARY, 1))
            .entryDateTime(LocalDateTime.of(2019, Month.JANUARY,
                1, 10, 11, 12))
            .build())
        .declarant(AdminApiDeclarant.builder()
            .bsn("999993653")
            .contactInformation(contactInfo)
            .build())
        .newAddress(AdminApiMunicipalAddress.builder()
            .street("Dorpstraat")
            .houseNumber(1)
            .houseLetter("A")
            .houseNumberAddition("4")
            .postalCode("1234AA")
            .residence("Koedijk")
            .municipality(AdminApiMunicipality.builder()
                .code("4567")
                .description("Alkmaar")
                .build())
            .addressFunction(AdminApiAddressFunctionType.LIVING_ADDRESS)
            .numberOfResidents(2)
            .destinationCurrentResidents("Don't know")
            .liveIn(AdminApiLiveIn.builder()
                .liveInApplicable(true)
                .consent(AdminApiLiveInConsentType.PENDING)
                .consenter(AdminApiConsenter.builder()
                    .bsn("999990639")
                    .contactInformation(contactInfo)
                    .build())
                .build())
            .mainOccupant(AdminApiMainOccupant.builder()
                .bsn("999993653")
                .contactInformation(contactInfo)
                .build())
            .build())
        .relocators(singletonList(AdminApiInterMunicipalRelocationPerson.builder()
            .declarationType(AdminApiDeclarationType.REGISTERED)
            .bsn("999993653")
            .contactInformation(contactInfo)
            .build()))
        .previousMunicipality(AdminApiMunicipality.builder()
            .code("8900")
            .description("Haarlem")
            .build())
        .build();
  }

}
