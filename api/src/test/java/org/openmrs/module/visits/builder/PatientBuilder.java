/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.builder;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;

import java.util.UUID;

public class PatientBuilder extends AbstractBuilder<Patient> {

  private Integer id;
  private String uuid;
  private Person person;
  private PatientIdentifier identifier;

  public PatientBuilder() {
    super();
    person = new PersonBuilder().build();
    identifier = new PatientIdentifierBuilder().build();
    uuid = UUID.randomUUID().toString();
  }

  @Override
  public Patient build() {
    Patient patient = new Patient(person);
    patient.setUuid(uuid);
    patient.setId(id == null ? getAndIncrementNumber() : id);
    patient.addIdentifier(identifier);
    return patient;
  }

  @Override
  public Patient buildAsNew() {
    return withId(null).build();
  }

  public PatientBuilder withId(Integer id) {
    this.id = id;
    return this;
  }

  public PatientBuilder withPerson(Person person) {
    this.person = person;
    return this;
  }

  public PatientBuilder withIdentifier(PatientIdentifier identifier) {
    this.identifier = identifier;
    return this;
  }

  public PatientBuilder withUuid(String uuid) {
    this.uuid = uuid;
    return this;
  }
}
