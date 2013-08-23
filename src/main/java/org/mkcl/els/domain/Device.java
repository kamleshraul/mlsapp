package org.mkcl.els.domain;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class Device extends BaseDomain {

}
