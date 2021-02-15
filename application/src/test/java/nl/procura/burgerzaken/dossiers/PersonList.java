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

package nl.procura.burgerzaken.dossiers;

import java.util.ArrayList;
import java.util.List;

import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.burgerzaken.gba.core.enums.GBARecStatus;
import nl.procura.gbaws.web.rest.v2.personlists.*;

public class PersonList {

  public static PersonListBuilder builder() {
    return new PersonListBuilder();
  }

  public static class PersonListBuilder extends IndexStructureBuilder<PersonListBuilder, CategoryBuilder> {

    public CategoryBuilder addCat(GBACat cat) {
      checkIndex(cat.getCode());
      CategoryBuilder categoryBuilder = new CategoryBuilder(this, cat);
      children.add(categoryBuilder);
      return categoryBuilder;
    }

    public CategoryBuilder getOrAddCat(GBACat cat) {
      if (is(cat.getCode())) {
        return get(cat.getCode());
      }
      checkIndex(cat.getCode());
      CategoryBuilder categoryBuilder = new CategoryBuilder(this, cat);
      children.add(categoryBuilder);
      return categoryBuilder;
    }
  }

  public static class CategoryBuilder extends IndexStructureBuilder<PersonListBuilder, SetBuilder> {

    private final GBACat cat;

    public CategoryBuilder(PersonListBuilder personListBuilder, GBACat cat) {
      super(personListBuilder, cat.getCode());
      this.cat = cat;
    }

    public SetBuilder addSet(int index) {
      checkIndex(index);
      SetBuilder setBuilder = new SetBuilder(this, index);
      children.add(setBuilder);
      return setBuilder;
    }

    public PersonListBuilder toPL() {
      return parent;
    }
  }

  public static class SetBuilder extends IndexStructureBuilder<CategoryBuilder, RecordBuilder> {

    public SetBuilder(CategoryBuilder categoryBuilder, int index) {
      super(categoryBuilder, index);
    }

    public RecordBuilder addRecord(int index, GBARecStatus status) {
      checkIndex(index);
      RecordBuilder recordBuilder = new RecordBuilder(this, status, index);
      children.add(recordBuilder);
      return recordBuilder;
    }

    public PersonListBuilder toPL() {
      return toCat().toPL();
    }

    public CategoryBuilder toCat() {
      return parent;
    }
  }

  public static class RecordBuilder extends IndexStructureBuilder<SetBuilder, ElemBuilder> {

    private final GBARecStatus status;

    public RecordBuilder(SetBuilder setBuilder, GBARecStatus status, int index) {
      super(setBuilder, index);
      this.status = status;
    }

    public RecordBuilder removeElem(GBAElem elemType) {
      children.removeIf(eb -> eb.type == elemType);
      return this;
    }

    public RecordBuilder addElem(GBAElem elemType, String value) {
      return addElem(elemType, value, "");
    }

    public RecordBuilder addElem(GBAElem elemType, long value) {
      return addElem(elemType, value, "");
    }

    public RecordBuilder addElem(GBAElem elemType, long value, String descr) {
      return addElem(elemType, "" + value, descr);
    }

    public RecordBuilder addElem(GBAElem elemType, String value, String descr) {
      checkIndex(elemType.getCode());
      children.add(new ElemBuilder(this, elemType, value, descr));
      return this;
    }

    public PersonListBuilder toPL() {
      return parent.toPL();
    }

    public CategoryBuilder toCat() {
      return parent.toCat();
    }

    public SetBuilder toSet() {
      return parent;
    }
  }

  public static class ElemBuilder extends IndexStructureBuilder<RecordBuilder, ElemBuilder> {

    private final GBAElem type;
    private final String  value;
    private final String  descr;

    public ElemBuilder(RecordBuilder recordBuilder, GBAElem type, String value, String descr) {
      super(recordBuilder, type.getCode());
      this.type = type;
      this.value = value;
      this.descr = descr;
    }
  }

  public abstract static class IndexStructureBuilder<PARENT extends StructureBuilder<?>, CHILD extends IndexStructureBuilder<?, ?>>
      extends StructureBuilder<PARENT> {

    protected final List<CHILD> children = new ArrayList<>();
    protected int               index;

    public IndexStructureBuilder() {
    }

    public IndexStructureBuilder(PARENT parent, int index) {
      super(parent);
      this.index = index;
    }

    public void checkIndex(int index) {
      children.stream()
          .filter(child -> child.index == index)
          .findFirst()
          .ifPresent(child -> {
            throw new IllegalArgumentException(
                String.format("Index: %d already exists in the collection", index));
          });
    }

    public boolean is(int index) {
      return children.stream()
          .anyMatch(set -> set.index == index);
    }

    public CHILD get(int index) {
      return children.stream()
          .filter(set -> set.index == index)
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("No set found with index: " + index));
    }
  }

  public abstract static class StructureBuilder<PARENT extends StructureBuilder<?>> {

    protected PARENT parent;

    public StructureBuilder() {
    }

    public StructureBuilder(PARENT parent) {
      this.parent = parent;
    }

    public GbaWsPersonList build() {
      if (parent != null) {
        return parent.build();
      }

      GbaWsPersonList pl = new GbaWsPersonList();
      PersonListBuilder plBuilder = (PersonListBuilder) this;

      for (CategoryBuilder catBuilder : plBuilder.children) {
        GbaWsPersonListCat plCat = new GbaWsPersonListCat();
        plCat.setCode(catBuilder.cat.getCode());
        plCat.setDescr(catBuilder.cat.getDescr());
        pl.getCats().add(plCat);

        int setIndex = 1;
        for (SetBuilder setBuilder : catBuilder.children) {
          GbaWsPersonListSet plSet = new GbaWsPersonListSet();
          plSet.setIndex(setIndex);
          plSet.setInternalIndex(setBuilder.index);
          plSet.setMostRecentMarriage(catBuilder.cat == GBACat.HUW_GPS && setIndex == 1);
          plCat.getSets().add(plSet);
          setIndex++;

          for (RecordBuilder recBuilder : setBuilder.children) {
            GbaWsPersonListRec plRec = new GbaWsPersonListRec();
            GbaWsPersonListRecStatus recStatus = new GbaWsPersonListRecStatus();
            recStatus.setCode(recBuilder.status.getCode());
            recStatus.setDescr(recBuilder.status.getDescr());
            plRec.setStatus(recStatus);
            plRec.setIndex(recBuilder.index);
            plSet.getRecords().add(plRec);

            for (ElemBuilder elemBuilder : recBuilder.children) {
              GbaWsPersonListElem elemRec = new GbaWsPersonListElem();
              elemRec.setCode(elemBuilder.type.getCode());
              elemRec.setDescr(elemBuilder.type.getDescr());
              GbaWsPersonListVal val = new GbaWsPersonListVal();
              val.setVal(elemBuilder.value);
              val.setDescr(elemBuilder.descr);
              elemRec.setValue(val);
              plRec.getElems().add(elemRec);
            }
          }
        }
      }
      return pl;
    }
  }
}
