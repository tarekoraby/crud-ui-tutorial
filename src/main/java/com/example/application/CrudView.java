package com.example.application;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class CrudView extends SplitLayout {

    private Grid<Person> grid;
    private final EditorLayout editorLayout;

    /**
     * The currently edited person
     */
    private Person person;

    ListDataProvider<Person> dataProvider;

    private final PersonRepository repo;

    private BeanValidationBinder<Person> binder;

    public CrudView(@Autowired PersonRepository repo) {
        this.repo = repo;

        configureGrid();

        editorLayout = new EditorLayout();
        configureBinding();

        setSizeFull();

        addToPrimary(grid);
        addToSecondary(editorLayout);
    }

    private void configureGrid() {
        // Auto create Grid's columns based on Person.class members
        grid = new Grid<>(Person.class, true);
        grid.setSizeFull();

        dataProvider = DataProvider.ofCollection(repo.findAll());
        grid.setDataProvider(dataProvider);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            Person person = event.getValue();
            if (person != null) {
                populateForm(person);
            } else {
                clearForm();
            }
        });
    }

    private void configureBinding() {
        binder = new BeanValidationBinder<>(Person.class);

        //Bind member fields found in the EditorLayout object.
        binder.bindInstanceFields(editorLayout);

        editorLayout.getDeleteButton().addClickListener(e -> {
            if (this.person != null) {
                repo.delete(this.person);
                dataProvider.getItems().remove(person);

                clearForm();
                refreshGrid();
                Notification.show("Person deleted.");
            }
        });

        editorLayout.getCancelButton().addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        editorLayout.getSaveButton().addClickListener(e -> {
            try {
                if (this.person == null) {
                    this.person = new Person();
                }
                binder.writeBean(this.person);

                repo.save(this.person);
                dataProvider.getItems().add(person);

                clearForm();
                refreshGrid();
                Notification.show("Person details stored.");
            } catch (ValidationException validationException) {
                Notification.show("Please enter a valid person details.");
            }
        });
    }

    void clearForm() {
        populateForm(null);
    }

    void populateForm(Person person) {
        this.person = person;
        binder.readBean(this.person);
    }

    public void refreshGrid() {
        grid.select(null);
        dataProvider.refreshAll();
    }
}

