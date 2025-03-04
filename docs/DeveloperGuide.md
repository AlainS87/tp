---
layout: page
title: Developer Guide
---

- Table of Contents
  {:toc}

---

## **Acknowledgements**

- {list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

---

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

---

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams in this document `docs/diagrams` folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.

</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The **_Architecture Diagram_** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**


**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/transact/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/transact/MainApp.java)) is in charge of the app launch and shut down.

- At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
- At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

- [**`UI`**](#ui-component): The UI of the App.
- [**`Logic`**](#logic-component): The command executor.
- [**`Model`**](#model-component): Holds the data of the App in memory.
- [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The _Sequence Diagram_ below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

- defines its _API_ in an `interface` with the same name as the Component.
- implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/transact/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/transact/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

- executes user commands using the `Logic` component.
- listens for changes to `Model` data so that the UI can be updated with the modified data.
- keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
- depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/transact/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteStaffCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteStaffCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteStaffCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:

- When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddStaffCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddStaffCommand`) which the `AddressBookParser` returns back as a `Command` object.
- All `XYZCommandParser` classes (e.g., `AddStaffCommandParser`, `DeleteStaffCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component

**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/transact/model/Model.java)


<img src="images/ModelClassDiagram.png" width="450" />

The `Model` component,

- stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
- stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
- stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
- does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>

### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/transact/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,

- can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
- inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
- depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `transact.commons` package.

---

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

- `VersionedAddressBook#commit()` — Saves the current address book state in its history.
- `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
- `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how the undo operation works:

![UndoSequenceDiagram](images/UndoSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</div>

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

- **Alternative 1 (current choice):** Saves the entire address book.

  - Pros: Easy to implement.
  - Cons: May have performance issues in terms of memory usage.

- **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  - Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  - Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_

---

## **Documentation, logging, testing, configuration, dev-ops**

- [Documentation guide](Documentation.md)
- [Testing guide](Testing.md)
- [Logging guide](Logging.md)
- [Configuration guide](Configuration.md)
- [DevOps guide](DevOps.md)

---

## **Appendix: Requirements**

### Product scope

**Target user profile**:


- has a need to manage a significant number of transactions
- can type fast
- prefers typing to mouse interactions
- is reasonably comfortable using CLI apps


# Developer Guide

## Introduction

Welcome to the development guide for **Tran$act**, a command-line interface (CLI) application designed to help accountants in small businesses efficiently manage their financial transactions and related data. This guide provides detailed instructions for developers on how to build and maintain Tran$act, ensuring it meets the needs and expectations of our target users.

## Target User Profile

Tran$act is specifically tailored for accountants and finance professionals in small businesses who have the following characteristics:

- **Need to manage a significant number of transactions**: Tran$act is designed to handle a high volume of financial transactions efficiently.

- **Fast typists**: Users are expected to be proficient in typing, as the application primarily relies on keyboard inputs for data entry and interaction.

- **Prefer CLI interactions**: Our users prefer command-line interactions over graphical user interfaces, as it allows for quicker data entry and navigation.


- **Reasonably comfortable with CLI apps**: Users should have a basic understanding of using command-line applications.

## User Stories
| Priority                 | As a …​                                           | I want to …​                                               | So that I can…​                                                        |
| ------------------------ | ------------------------------------------------- | ---------------------------------------------------------- | ---------------------------------------------------------------------- |
| `* * *`                  | new user                                          | see usage instructions                                     | refer to instructions when I forget how to use the App                 |
| `* * *`                  | user                                              | add a new person                                           |                                                                        |
| `* * *`                  | user                                              | delete a person                                            | remove entries that I no longer need                                   |
| `* * *`                  | user                                              | find a person by name                                      | locate details of persons without having to go through the entire list |
| `* *`                    | user                                              | hide private contact details                               | minimize chance of someone else seeing them by accident                |
| `*`                      | user with many persons in the address book        | sort persons by name                                       | locate a person easily                                                 |
| Transaction Recording    |
| `* * *`                  | user                                  | add a new transaction                                      |                                                                        |
| `* * *`                  | user                                  | remove a transaction                                       |                                                                        |
| `* * *`                  | user                                  | view all transactions                                      |                                                                        |
| `* *`                    | user                                  | edit transactions through the software                     |                                                                        |
| `* *`                    | user                                  | edit transactions without opening the software             |                                                                        |
| Financial Reporting      |
| `*`                      | user who needs to analyze transactions | have a variety of financial reports                        |                                                                        |
| `*`                      | user who needs to share data with others | generate reports downloadable in common formats (PDF, CSV) |                                                                        |
| `*`                      | user                                  | restore from a backup                                      | undo large changes                                                     |
| Data Security and Backup |
| `*`                      | user who needs to keep sensitive data confidental | encrypt the data                                           | ensure the security and privacy of financial data                      |
| `*`                      | user who does not have reliable hardware | have automated backups                                     | prevent unnessary data loss                                            |
| `*`                      | user                                  | restore from a backup                                      | undo large changes                                                     |

### Transaction Recording

1. **Adding a Transaction**
    - As an accountant, I want to add a new financial transaction quickly.
    - I should be able to specify the amount, type (revenue or expense), date, and optionally, the associated person for the transaction.

2. **Removing a Transaction**
    - As an accountant, I want to delete a transaction when necessary.
    - I should be able to remove a transaction from the records to correct mistakes or manage data.

3. **Viewing All Transactions**
    - As an accountant, I want to see a list of all recorded transactions for reference.
    - This list will provide an overview of all financial activities in one place.

4. **Editing a Transaction**
    - As an accountant, I need the ability to edit transaction details.
    - I should be able to make corrections or updates to transaction records as needed.

5. **Restoring Deleted Transactions**
    - As an accountant, I want a safety net for accidental deletions.
    - I should be able to retrieve transactions I have mistakenly deleted from a "bin" or archive.

6. **Fast Data Entry**
    - As a fast typist, I want shortcuts and efficient data entry methods.
    - This will enable me to record transactions, including income and expenses, quickly via the CLI.

### Dashboard Overview

1. **Dashboard Display**
    - As an accountant, I want to see a clear and concise dashboard upon opening the app.
    - The dashboard should display total income, total expenses, net profit for the selected period (usually monthly), and a breakdown of expenses by sector to improve cost efficiency.

### Financial Reporting

1. **Access to Financial Reports**
    - As an accountant who analyzes transactions, I need access to various financial reports.
    - I should be able to generate income statements, balance sheets, and cash flow statements.

2. **Customizable Reports**
    - As an accountant who shares data with stakeholders, I want to generate customizable reports.
    - I should be able to create reports in common formats (PDF, CSV, Excel) to share with others.

### Data Security and Backup

1. **Data Security**
    - As a user who values data privacy, I expect the app to secure financial data.
    - The app should implement security measures, potentially including encryption, to protect sensitive information.

2. **Automated Backups**
    - As a user concerned about data loss, I want the option for automated backups.
    - The app should allow me to set up automated backups to prevent data loss due to hardware issues.

3. **Undo and Restore**
    - As a user prone to mistakes, I need the ability to undo actions or restore from backups.
    - This feature will help me recover from errors or data corruption.

### Address Book

1. **Importing Staff List**
    - As a user who needs efficiency, I want to import a staff list into the address book.
    - This will help me quickly access contact information for employees and associates.

2. **Adding and Editing People**
    - As a user who manages relationships, I need to add, edit, and remove people from the address book.
    - This allows me to keep the address book up to date and accurate.

3. **Viewing Address Book**
    - As a user who relies on contact information, I want to view the entire address book.
    - This provides easy access to contact details for individuals in the address book.

## Use Cases

### Use Case 1: Adding a Transaction

**Actor:** Accountant

**Preconditions:** The accountant is logged into the Tran$act application.

**Description:**
1. The accountant specifies the transaction details, including the amount, type (revenue or expense), date, and optionally, the associated person.
2. The system validates the input data.
3. The system records the transaction in the database.

**Extensions:**
- If the input data is invalid, the system displays an error message.

### Use Case 2: Removing a Transaction

**Actor:** Accountant

**Preconditions:** The accountant is logged into the Tran$act application.

**Description:**
1. The accountant selects a transaction to remove from the records.
2. The system confirms the removal with the accountant.
3. The system removes the transaction from the database.

### Use Case 3: Viewing All Transactions

**Actor:** Accountant

**Preconditions:** The accountant is logged into the Tran$act application.

**Description:**
1. The accountant selects the option to view all transactions.
2. The system retrieves and displays a list of all recorded transactions.

### Use Case 4: Editing a Transaction

**Actor:** Accountant

**Preconditions:** The accountant is logged into the Tran$act application.

**Description:**
1. The accountant selects a transaction to edit.
2. The system allows the accountant to modify the transaction details.
3. The system saves the updated transaction in the database.

**Extensions:**
- If the input data is invalid, the system displays an error message.

### Use Case 5: Restoring Deleted Transactions
**Value proposition**: Quickly add company inflow and outflow via the CLI. Easily keep track of company profits and expenses associated with staff and visualize them.

**Actor:** Accountant

**Preconditions:** The accountant is logged into the Tran$act application.

**Description:**
1. The accountant selects the option to restore deleted transactions.
2. The system presents a list of previously deleted transactions.
3. The accountant selects a transaction to restore.
4. The system restores the selected transaction to the active records.

### Use Case 6: Dashboard Display


**Actor:** Accountant

**Preconditions:** The accountant is logged into the Tran$act application.

**Description:**
1. Upon opening the app, the system displays a dashboard.
2. The dashboard shows total income, total expenses, net profit for the selected period (usually monthly), and a breakdown of expenses by sector.

### Use

Case 7: Access to Financial Reports

**Actor:** Accountant

**Preconditions:** The accountant is logged into the Tran$act application.

**Description:**
1. The accountant selects the option to access financial reports.
2. The system generates and displays financial reports, including income statements, balance sheets, and cash flow statements.


### Use Case 8: Customizable Reports

**Actor:** Accountant

**Preconditions:** The accountant is logged into the Tran$act application.

**Description:**
1. The accountant selects the option to generate customizable reports.
2. The system provides tools for the accountant to customize the report parameters.
3. The system generates and displays the customized report in common formats (PDF, CSV, Excel).

## Non-functional Requirements (NFR)

- Tran$act should support the storage of at least 1000 transactions per month to accommodate the needs of small businesses.
-  Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
-  Should be able to hold up to 1000 transactions without a noticeable sluggishness in performance for typical usage.
-  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.

## Glossary

- **Transaction**: An exchange of money, which includes buying and selling activities.
- **Transactor/Party**: The person or entity that carried out the transaction or is related to it.
- **Income**: Money received, such as sales revenue.
- **Expense**: Costs incurred, including staff salaries and product costs.
- **Mainstream OS**: Windows, Linux, Unix, OS-X
- **Private contact detail**: A contact detail that is not meant to be shared with others
- **Transaction**: An exchange of money (e.g. Buying, Selling)
- **Transactor/Party**: Person that carried out the transaction or is related to the transaction
- **Income**: Money received (e.g. Sales of product)
- **Expense**: Costs incurred (e.g. Staff salary, cost of product)


---

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
      Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
