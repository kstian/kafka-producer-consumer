package com.mitrais.service1.ui;

import com.mitrais.service1.domain.Member;
import com.mitrais.service1.service.MemberService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class MemberForm extends VerticalLayout implements KeyNotifier {
    private Member member;
    private MemberService memberService;
    TextField firstName = new TextField("First name");
	TextField lastName = new TextField("Last name");

    Button save = new Button("Save", VaadinIcon.CHECK.create());
	Button cancel = new Button("Cancel");
    HorizontalLayout actions = new HorizontalLayout(save, cancel);

    Binder<Member> binder = new Binder<>(Member.class);
	
    private ChangeHandler changeHandler;

    public MemberForm(MemberService memberService){
        this.memberService = memberService;
        add(firstName, lastName, actions);

		// bind using naming convention
		binder.bindInstanceFields(this);

		// Configure and style components
		setSpacing(true);

		save.getElement().getThemeList().add("primary");

		addKeyPressListener(Key.ENTER, e -> save());

		// wire action buttons to save, delete and reset
		save.addClickListener(e -> save());
		cancel.addClickListener(e -> editMember(member));
		setVisible(false);
        
    }

    void save() {
        memberService.save(member);
		changeHandler.onChange();
	}

    public final void editMember(Member member) {
		if (member == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = member.getId() != null;
		
        if (persisted) {
			this.member= memberService.findById(member.getId()).orElse(member);
		}
		else {
			this.member = member;
		}
		cancel.setVisible(persisted);

		// Bind customer properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(member);

		setVisible(true);

		// Focus first name initially
		firstName.focus();
	}

    public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either save or delete
		// is clicked
		changeHandler = h;
	}

	public interface ChangeHandler {
		void onChange();
	}
}
