package com.mitrais.service1.ui;

import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.mitrais.service1.domain.Member;
import com.mitrais.service1.service.MemberService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends VerticalLayout{
    
    private final MemberService memberService;

	// UI Components
    private final MemberForm memberForm;
    private final Grid<Member> grid;
    private final TextField filter = new TextField();
	private final Button addNewBtn = new Button("Add", VaadinIcon.PLUS.create());
    private final Button generateBtn = new Button("Generate", VaadinIcon.AUTOMATION.create());
	private final Button randomUpdateBtn = new Button("Random Update", VaadinIcon.DATABASE.create());

    public MainView(MemberService memberService, MemberForm memberForm){
        this.memberService = memberService;
        this.memberForm = memberForm;
        this.grid = new Grid<>(Member.class);
        // build layout
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn, generateBtn, randomUpdateBtn);
		actions.setSizeFull();
		add(actions, grid, memberForm);
        
		grid.setHeight("300px");
		grid.setColumns("id", "firstName", "lastName");

		filter.setPlaceholder("Filter");
        filter.setValueChangeMode(ValueChangeMode.EAGER);
		filter.addValueChangeListener(e -> search(e.getValue()));
        
		// Connect selected Member to editor or hide if none is selected
		grid.asSingleSelect().addValueChangeListener(e -> {
			memberForm.editMember(e.getValue());
		});

		// Instantiate and edit new Member the new button is clicked
		addNewBtn.addClickListener(e -> this.memberForm.editMember(new Member()));

		generateBtn.addClickListener(e-> {
			e.getSource().setEnabled(false);
			UI ui = UI.getCurrent(); 
			this.memberService.generateData().addCallback(
				new ListenableFutureCallback<>() {

					@Override
					public void onSuccess(Void result) {
						ui.access(
                            () -> {
                                // setup for error
                                generateBtn.setEnabled(true);
								search(null);
                            }
                        );						
					}

					@Override
					public void onFailure(Throwable ex) {
						ui.access(
                            () -> {
                                // setup for task completed
                                generateBtn.setEnabled(true);
								search(null);
                            }
                        );
						
					}
				
				}
			);
		});
		randomUpdateBtn.addClickListener(e-> {
			e.getSource().setEnabled(false);
			UI ui = UI.getCurrent(); 
			this.memberService.randomUpdate().addCallback(
				new ListenableFutureCallback<>() {

					@Override
					public void onSuccess(Void result) {
						ui.access(
                            () -> {
                                // setup for error
                                e.getSource().setEnabled(true);
								search(null);
                            }
                        );						
					}

					@Override
					public void onFailure(Throwable ex) {
						ui.access(
                            () -> {
                                // setup for task completed
                                e.getSource().setEnabled(true);
								search(null);
                            }
                        );
						
					}
				
				}
			);
		});
		// Listen changes made by the editor, refresh data from backend
		this.memberForm.setChangeHandler(() -> {
			memberForm.setVisible(false);
			search(filter.getValue());
		});

		// Initialize listing
		search(null);
    }

    void search(String value){
        if (!StringUtils.hasText(value)) {
			grid.setItems(memberService.list());
		}
		else {
			grid.setItems(memberService.findByFirstNameOrLastName(value));
		}
    }
}
