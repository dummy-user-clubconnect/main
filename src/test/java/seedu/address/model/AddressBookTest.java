package seedu.address.model;

import static org.junit.Assert.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.MANDATORY_GROUP;
import static seedu.address.logic.commands.CommandTestUtil.NON_EXISTENT_GROUP;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_UNUSED;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.group.Group;
import seedu.address.model.group.exceptions.GroupCannotBeRemovedException;
import seedu.address.model.group.exceptions.GroupNotFoundException;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.exceptions.TagNotFoundException;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

public class AddressBookTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final AddressBook addressBook = new AddressBook();
    private final AddressBook addressBookWithBobAndAmy = new AddressBookBuilder().withPerson(BOB).withPerson(AMY)
            .build();

    @Test
    public void constructor() {
        assertEquals(Collections.emptyList(), addressBook.getPersonList());
        assertEquals(Collections.emptyList(), addressBook.getTagList());
    }

    @Test
    public void removeGroup_nonExistentGroup_unchangedAddressBook() throws Exception {
        try {
            addressBookWithBobAndAmy.removeGroup(new Group(NON_EXISTENT_GROUP));
        } catch (GroupNotFoundException gnfe) {
            AddressBook expectedAddressBook = new AddressBookBuilder().withPerson(BOB).withPerson(AMY).build();
            assertEquals(expectedAddressBook, addressBookWithBobAndAmy);
        }
    }

    @Test
    public void removeGroup_mandatoryGroup_unchangedAddressBook() throws Exception {
        try {
            addressBookWithBobAndAmy.removeGroup(new Group(MANDATORY_GROUP));
        } catch (GroupCannotBeRemovedException e) {
            AddressBook expectedAddressBook = new AddressBookBuilder().withPerson(BOB).withPerson(AMY).build();
            assertEquals(expectedAddressBook, addressBookWithBobAndAmy);
        }
    }

    @Test
    public void removeGroup_atLeastOnePersonInGroup_groupRemoved() throws Exception {
        addressBookWithBobAndAmy.removeGroup(new Group(VALID_GROUP_BOB));

        Person bobNotInLogistics = new PersonBuilder(BOB).withGroup().build();
        Person amyNotInLogistics = new PersonBuilder(AMY).build();
        AddressBook expectedAddressBook = new AddressBookBuilder().withPerson(bobNotInLogistics)
                .withPerson(amyNotInLogistics).build();

        assertEquals(expectedAddressBook, addressBookWithBobAndAmy);
    }

    @Test
    public void updatePerson_detailsChanged_personUpdated() throws Exception {
        AddressBook updatedToBob = new AddressBookBuilder().withPerson(AMY).build();
        updatedToBob.updatePerson(AMY, BOB);

        AddressBook expectedAddressBook = new AddressBookBuilder().withPerson(BOB).build();

        assertEquals(expectedAddressBook, updatedToBob);
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        addressBook.resetData(null);
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsAssertionError() {
        // Repeat ALICE twice
        List<Person> newPersons = Arrays.asList(ALICE, ALICE);
        List<Tag> newTags = new ArrayList<>(ALICE.getTags());
        AddressBookStub newData = new AddressBookStub(newPersons, newTags);

        thrown.expect(AssertionError.class);
        addressBook.resetData(newData);
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        thrown.expect(UnsupportedOperationException.class);
        addressBook.getPersonList().remove(0);
    }

    @Test
    public void getTagList_modifyList_throwsUnsupportedOperationException() {
        thrown.expect(UnsupportedOperationException.class);
        addressBook.getTagList().remove(0);
    }

    @Test
    public void updatePerson_detailsChanged_personsAndTagsListUpdated() throws Exception {
        AddressBook addressBookUpdatedToAmy = new AddressBookBuilder().withPerson(BOB).build();
        addressBookUpdatedToAmy.updatePerson(BOB, AMY);

        AddressBook expectedAddressBook = new AddressBookBuilder().withPerson(AMY).build();

        assertEquals(expectedAddressBook, addressBookUpdatedToAmy);
    }

    @Test
    public void deleteTag_nonExistentTag_addressBookUnchanged() {
        try {
            addressBookWithBobAndAmy.deleteTag(new Tag(VALID_TAG_UNUSED));
        } catch (TagNotFoundException tnfe) {
            AddressBook expectedAddressBook = new AddressBookBuilder().withPerson(BOB).withPerson(AMY).build();
            assertEquals(expectedAddressBook, addressBookWithBobAndAmy);
        }
    }

    @Test
    public void deleteTag_tagUsedByMultiplePersons_tagRemoved() throws Exception {
        addressBookWithBobAndAmy.deleteTag(new Tag(VALID_TAG_FRIEND));

        Person amyWithoutFriendTag = new PersonBuilder(AMY).withTags().build();
        Person bobWithoutFriendTag = new PersonBuilder(BOB).withTags(VALID_TAG_HUSBAND).build();
        AddressBook expectedAddressBook = new AddressBookBuilder().withPerson(bobWithoutFriendTag)
                .withPerson(amyWithoutFriendTag).build();

        assertEquals(expectedAddressBook, addressBookWithBobAndAmy);
    }

    /**
     * A stub ReadOnlyAddressBook whose persons and tags lists can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();
        private final ObservableList<Tag> tags = FXCollections.observableArrayList();

        AddressBookStub(Collection<Person> persons, Collection<? extends Tag> tags) {
            this.persons.setAll(persons);
            this.tags.setAll(tags);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }

        @Override
        public ObservableList<Tag> getTagList() {
            return tags;
        }
    }

}
