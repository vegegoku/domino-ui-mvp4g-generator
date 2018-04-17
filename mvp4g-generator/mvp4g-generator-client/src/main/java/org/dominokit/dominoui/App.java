package org.dominokit.dominoui;

import com.google.gwt.core.client.EntryPoint;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.Text;
import org.dominokit.domino.ui.alerts.Alert;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.button.ButtonsToolbar;
import org.dominokit.domino.ui.button.IconButton;
import org.dominokit.domino.ui.button.group.ButtonsGroup;
import org.dominokit.domino.ui.cards.Card;
import org.dominokit.domino.ui.column.Column;
import org.dominokit.domino.ui.forms.CheckBox;
import org.dominokit.domino.ui.forms.DropDown;
import org.dominokit.domino.ui.forms.DropDownOption;
import org.dominokit.domino.ui.forms.TextBox;
import org.dominokit.domino.ui.header.BlockHeader;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.layout.Layout;
import org.dominokit.domino.ui.lists.ListGroup;
import org.dominokit.domino.ui.lists.ListItem;
import org.dominokit.domino.ui.modals.ModalDialog;
import org.dominokit.domino.ui.row.Row;
import org.dominokit.domino.ui.style.Background;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.themes.Theme;
import org.dominokit.domino.ui.utils.ElementUtil;
import org.jboss.gwt.elemento.core.EventType;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.jboss.gwt.elemento.core.Elements.div;
import static org.jboss.gwt.elemento.core.Elements.h;

public class App implements EntryPoint {

    private Column screenColumn = Column.create()
            .onLarge(Column.OnLarge.three)
            .onMedium(Column.OnMedium.three)
            .onSmall(Column.OnSmall.twelve)
            .onXSmall(Column.OnXSmall.twelve);


    public void onModuleLoad() {
        Layout layout = setUpLayout();


        TextBox groupIdTextBox = TextBox.create("Group Id").floating();
        TextBox artifactIdTextBox = TextBox.create("Artifact Id").floating();

        DropDown gwtVersionDropDown = DropDown.create().addOption(DropDownOption.create("2.8.2", "2.8.2"))
                .selectAt(0);
        DropDown widgetSetDropDown = DropDown.create()
                .addOption(DropDownOption.create("elemento", "Use Elemento widgets"))
                .addOption(DropDownOption.create("gwt", "Use GWT widgets (will not work with J2CL / GWT 3)"))
                .addOption(DropDownOption.create("gxt", "Use GXT GPL widgets (will not work with J2CL / GWT 3)"));
//                .selectAt(0);

        CheckBox loaderClassCheckBox = CheckBox.create("Generate Application Loader class").filledIn().check();
        CheckBox debugCheckBox = CheckBox.create("Generate Debug support (in development mode)").filledIn().check();
        CheckBox historyCheckBox = CheckBox.create("Application supports history handling").filledIn().check();
        CheckBox bookMarkingCheckBox = CheckBox.create("Application handles history token on start (book marking)").filledIn().check();


        Column column = Column.create()
                .onXSmall(Column.OnXSmall.twelve)
                .onSmall(Column.OnSmall.twelve)
                .onMedium(Column.OnMedium.six)
                .onLarge(Column.OnLarge.six);

        layout.getContentPanel().appendChild(Card.create("Project meta data")
                .setHeaderBackground(Background.BLUE_GREY)
                .appendContent(Row.create()
                        .addColumn(column.copy().addElement(groupIdTextBox.asElement()))
                        .addColumn(column.copy().addElement(artifactIdTextBox.asElement()))
                        .asElement())
                .appendContent(Row.create()
                        .addColumn(column.copy().addElement(BlockHeader.create("GWT version").asElement()))
                        .addColumn(column.copy().addElement(BlockHeader.create("Widget set").asElement()))
                        .asElement())
                .appendContent(Row.create()
                        .addColumn(column.copy().addElement(gwtVersionDropDown.asElement()))
                        .addColumn(column.copy().addElement(widgetSetDropDown.asElement()))
                        .asElement())
                .asElement());

        layout.getContentPanel().appendChild(Card.create("Application meta data")
                .setHeaderBackground(Background.BLUE_GREY)
                .appendContent(Row.create()
                        .addColumn(column.copy().addElement(loaderClassCheckBox.asElement()))
                        .addColumn(column.copy().addElement(debugCheckBox.asElement()))
                        .asElement())
                .appendContent(Row.create()
                        .addColumn(column.copy().addElement(historyCheckBox.asElement()))
                        .addColumn(column.copy().addElement(bookMarkingCheckBox.asElement()))
                        .asElement())
                .asElement());


        ListGroup<ScreenMetaData> screenListGroup = ListGroup.create();


        ScreenMetaData screen01 = new ScreenMetaData("screen01", "R2D2", "presenter", true, false);
        ScreenMetaData screen02 = new ScreenMetaData("screen02", "C3P0", "framework", false, false);
        ScreenMetaData screen03 = new ScreenMetaData("screen03", "BB8", "framework", false, true);

        screenListGroup
                .appendItem(screenListGroup.createItem(new ScreenMetaData(), "")
                        .disable()
                        .setBackground(Background.GREY)
                        .appendContent(Row.create()
                                .addColumn(screenColumn.copy().addElement(new Text("History token")))
                                .addColumn(screenColumn.copy().addElement(new Text("Start screen")))
                                .addColumn(screenColumn.copy().addElement(new Text("Confirmation")))
                                .addColumn(screenColumn.copy().addElement(new Text("Creation method")))
                                .asElement()))
                .appendItem(new ScreenListItem(screenListGroup, screen01))
                .appendItem(new ScreenListItem(screenListGroup, screen02))
                .appendItem(new ScreenListItem(screenListGroup, screen03));

        screenListGroup.asElement().style.setProperty("margin-top", "10px");
        screenListGroup.asElement().style.setProperty("margin-bottom", "10px");


        ScreenDialog screenDialog = new ScreenDialog();

        screenDialog.modal.onClose(() -> {
            ScreenMetaData screenMetaData = screenDialog.listItem.getValue();
            if (nonNull(screenMetaData)) {
                if (screenListGroup.getSelectedValues().contains(screenMetaData)) {
                } else {
                    screenListGroup.appendItem(screenDialog.listItem);
                }
                screenDialog.listItem.update();
            }
        });

        IconButton addButton = IconButton.create(Icons.ALL.add());
        addButton.setColor(Color.GREEN);
        addButton.addClickListener(evt -> {
            screenDialog.show("Add new screen", new ScreenListItem(screenListGroup, new ScreenMetaData()));
        });

        IconButton editButton = IconButton.create(Icons.ALL.mode_edit());
        editButton.setColor(Color.INDIGO).disable();

        editButton.addClickListener(evt -> {
            if (!screenListGroup.getSelectedItems().isEmpty())
                screenDialog.show("Add new screen", (ScreenListItem) screenListGroup.getSelectedItems().get(0));
        });

        IconButton removeButton = IconButton.create(Icons.ALL.delete());
        removeButton.setColor(Color.RED).disable();
        removeButton.addClickListener(evt -> screenListGroup.removeSelected());

        screenListGroup.addSelectionChangeHandler(item -> {
            if (screenListGroup.getSelectedItems().isEmpty()) {
                editButton.disable();
                removeButton.disable();
            } else {
                editButton.enable();
                removeButton.enable();
            }
        });

        Button generateButton = Button.createPrimary("GENERATE").large().block();

        Column generateColumn = Column.create()
                .onXSmall(Column.OnXSmall.twelve)
                .onSmall(Column.OnSmall.twelve)
                .onMedium(Column.OnMedium.four)
                .onLarge(Column.OnLarge.four);

        layout.getContentPanel().appendChild(Card.create("Screen meta data")
                .setHeaderBackground(Background.BLUE_GREY)
                .appendContent(ButtonsToolbar.create()
                        .addGroup(ButtonsGroup.create()
                                .large()
                                .addButton(addButton))
                        .addGroup(ButtonsGroup.create()
                                .large()
                                .addButton(editButton)
                                .addButton(removeButton))
                        .asElement())
                .appendContent(screenListGroup.asElement())
                .appendContent(Row.create()
                        .addColumn(generateColumn.copy())
                        .addColumn(generateColumn.copy().addElement(generateButton.asElement()))
                        .addColumn(generateColumn.copy())
                        .asElement())
                .asElement());
    }

    private Layout setUpLayout() {
        Layout layout = Layout.create("TODO-List")
                .removeLeftPanel().show(Theme.BLUE_GREY);

        layout.getNavigationBar().asElement().style.setProperty("height", "150px");
        layout.getNavigationBar().asElement().style.setProperty("text-align", "center");
        layout.getNavigationBar().asElement().style.setProperty("position", "sticky");

        layout.getContentSection().asElement().style.setProperty("margin-top", "5px");
        layout.getContentSection().asElement().classList.add("content-margin");


        ElementUtil.clear(layout.getNavigationBar().asElement());
        layout.getNavigationBar().asElement().appendChild(h(2).style("line-height: 50px").textContent("Mvp4g2 Initializer").asElement());
        layout.getNavigationBar().asElement().appendChild(h(4).style("line-height: 50px").textContent("generate your mvp4g2 application ...").asElement());
        return layout;
    }

    private class ScreenDialog {
        private ModalDialog modal = ModalDialog.create().setAutoClose(false);

        private HTMLDivElement errorsDiv = div().asElement();
        private TextBox screeNameBox = TextBox.create("Screen name").floating();
        private TextBox historyTokenBox = TextBox.create("History token").floating();
        private DropDown creationMethodDropDown = DropDown.create()
                .addOption(DropDownOption.create("framework", "View is created by framework"))
                .addOption(DropDownOption.create("presenter", "View is created by presenter"))
                .selectAt(0);
        private CheckBox startScreenCheckBox = CheckBox.create("show this screen as start screen in case there is no history");
        private CheckBox confirmationCheckBox = CheckBox.create("implement confirmation for this screen");
        private Button okButton = Button.create("SAVE").linkify();
        private Button cancelButton = Button.create("CANCEL").linkify();

        private ScreenListItem listItem;

        public ScreenDialog() {
            modal.appendContent(errorsDiv)
                    .appendContent(screeNameBox.asElement())
                    .appendContent(historyTokenBox.asElement())
                    .appendContent(creationMethodDropDown.asElement())
                    .appendContent(startScreenCheckBox.asElement())
                    .appendContent(confirmationCheckBox.asElement())
                    .appendFooterContent(okButton.asElement())
                    .appendFooterContent(cancelButton.asElement());

            okButton.asElement().style.setProperty("min-width", "120px");

            screeNameBox.getInputElement().addEventListener(EventType.change.getName(), evt -> {
                if (screeNameBox.getValue().isEmpty())
                    errorsDiv.appendChild(Alert.error().appendText("Screen name is required").asElement());
                else
                    ElementUtil.clear(errorsDiv);
            });


            okButton.addClickListener(evt -> {
                ElementUtil.clear(errorsDiv);
                if (isNull(screeNameBox.getValue()) || screeNameBox.getValue().isEmpty()) {
                    errorsDiv.appendChild(Alert.error().appendText("Screen name is required").asElement());
                    return;
                }

                ScreenMetaData screenMetaData = listItem.getValue();
                screenMetaData.screenName = screeNameBox.getValue();
                screenMetaData.historyName = historyTokenBox.getValue();
                screenMetaData.viewCreationMethod = creationMethodDropDown.getValue();
                screenMetaData.isStartScreen = startScreenCheckBox.getValue();
                screenMetaData.isImplementConfirmation = confirmationCheckBox.getValue();
                this.modal.close();
            });

            cancelButton.addClickListener(evt -> {
                this.listItem = null;
                modal.close();
            });

            DomGlobal.document.body.appendChild(modal.asElement());
        }

        public void show(String title, ScreenListItem listItem) {
            modal.setTitle(title);
            if (nonNull(listItem)) {
                this.listItem = listItem;
                ScreenMetaData screenMetaData = listItem.getValue();
                screeNameBox.setValue(screenMetaData.screenName);
                historyTokenBox.setValue(screenMetaData.historyName);
                creationMethodDropDown.setValue(screenMetaData.viewCreationMethod);
                startScreenCheckBox.setValue(screenMetaData.isStartScreen);
                confirmationCheckBox.setValue(screenMetaData.isImplementConfirmation);

                if (nonNull(screenMetaData.screenName) && !screenMetaData.screenName.isEmpty())
                    okButton.setContent("SAVE");
                else
                    okButton.setContent("ADD");
                modal.open();
            }
        }
    }

    private class ScreenListItem extends ListItem<ScreenMetaData> {
        private final ScreenMetaData screenMetaData;
        private Text history = new Text();
        private Text startScreen = new Text();
        private Text confirmation = new Text();
        private Text creationMethod = new Text();

        public ScreenListItem(ListGroup<ScreenMetaData> group, ScreenMetaData screenMetaData) {
            super(screenMetaData, group);
            this.screenMetaData = screenMetaData;

            this.setHeading(screenMetaData.screenName)
                    .appendContent(Row.create()
                            .addColumn(screenColumn.copy().addElement(history))
                            .addColumn(screenColumn.copy().addElement(startScreen))
                            .addColumn(screenColumn.copy().addElement(confirmation))
                            .addColumn(screenColumn.copy().addElement(creationMethod))
                            .asElement());
            update();
        }

        public void update() {
            setHeading(screenMetaData.screenName);
            history.textContent = screenMetaData.historyName;
            startScreen.textContent = asYesNo(screenMetaData.isStartScreen);
            confirmation.textContent = asYesNo(screenMetaData.isImplementConfirmation);
            creationMethod.textContent = screenMetaData.viewCreationMethod;
        }
    }

    private String asYesNo(boolean value) {
        return value ? "YES" : "NO";
    }

    private class ScreenMetaData {

        private String screenName = "";
        private String historyName = "";
        private String viewCreationMethod = "framework";
        private boolean isStartScreen = false;
        private boolean isImplementConfirmation = false;

        public ScreenMetaData() {
        }

        public ScreenMetaData(String screenName, String historyName, String viewCreationMethod, boolean isStartScreen, boolean isImplementConfirmation) {
            this.screenName = screenName;
            this.historyName = historyName;
            this.viewCreationMethod = viewCreationMethod;
            this.isStartScreen = isStartScreen;
            this.isImplementConfirmation = isImplementConfirmation;
        }
    }
}
