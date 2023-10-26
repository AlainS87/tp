package transact.logic.commands;

import static java.util.Objects.requireNonNull;

import transact.model.AddressBook;
import transact.model.Model;
import transact.ui.MainWindow;

/**
 * Clears the transaction list sort.
 */
public class ClearSortCommand extends Command {

    public static final String COMMAND_WORD = "clearsort";
    public static final String MESSAGE_SUCCESS = "Transaction sort has been cleared!";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setAddressBook(new AddressBook());
        model.updateFilteredTransactionList(Model.COMPARATOR_ASC_BY_ID);

        return new CommandResult(MESSAGE_SUCCESS, MainWindow.TabWindow.TRANSACTIONS);
    }
}
