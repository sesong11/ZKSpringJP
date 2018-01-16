package com.sample.ZKSpringJPA.viewmodel.request.leaveform;

import com.sample.ZKSpringJPA.anotation.Feature;
import com.sample.ZKSpringJPA.entity.employment.*;
import com.sample.ZKSpringJPA.entity.request.*;
import com.sample.ZKSpringJPA.entity.request.approval.Approval;
import com.sample.ZKSpringJPA.entity.request.approval.ApprovalType;
import com.sample.ZKSpringJPA.entity.request.leaveform.LeaveForm;
import com.sample.ZKSpringJPA.entity.request.leaveform.LeaveType;
import com.sample.ZKSpringJPA.services.employment.EmployeeService;
import com.sample.ZKSpringJPA.services.request.ApprovalService;
import com.sample.ZKSpringJPA.services.request.LeaveFormService;
import com.sample.ZKSpringJPA.services.request.RequestService;
import com.sample.ZKSpringJPA.utils.StandardFormat;
import com.sample.ZKSpringJPA.utils.UserCredentialService;
import com.sample.ZKSpringJPA.viewmodel.utils.ListPagingVM;
import com.sample.ZKSpringJPA.viewmodel.utils.ViewModel;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.*;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Window;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.sql.Timestamp;
import java.util.*;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
@Feature(
        view = "/view/request/forms/leave-form.zul",
        uuid = "leave-form",
        menuOrder = "3.0.1",
        displayName = "Leave Request",
        menuIcon = "tag"
)
public class LeaveFormVM extends ViewModel {
    //region > Inject Services
    @WireVariable
    private EmployeeService employeeService;

    @WireVariable
    private RequestService requestService;

    @WireVariable
    private LeaveFormService leaveFormService;

    @WireVariable
    private ApprovalService approvalService;

    @WireVariable
    private UserCredentialService userCredentialService;

    ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
    private javax.validation.Validator validator = vf.getValidator();

    private Set<ConstraintViolation<RequestForm>> violations;
    //endregion

    //region > Fields
    @Setter
    @Getter
    private LeaveForm form;

    @Getter
    @Setter
    private Employee requestFor;

    @Getter
    @Setter
    private Approval relief;

    @Getter
    @Setter
    private Approval supervisor;

    @Getter
    @Setter
    private Approval manager;

    @Getter
    @Setter
    private List<LeaveType> leaveTypes = new ListModelList<>(LeaveType.values());

    @Getter
    @Setter
    private List<RequestPriority> requestPriorities = new ListModelList<>(RequestPriority.values());

    @Getter
    private final String standardDateTimeFormat = StandardFormat.getStandardDateTimeFormat();
    //endregion

    //region > Constructor
    @Init
    public void init() {
        String sid = Executions.getCurrent().getParameter("id");
        if (sid == null) {
            form = new LeaveForm();
            Request request = new Request();
            request.setFormType(FormType.LEAVE_REQUEST);
            form.setRequest(request);
            //person who relief requester job during his/her leave.
            relief = new Approval();
            relief.setSortedIndex(1);
            relief.setId(1L);
            relief.setDecisionStatus(DecisionStatus.AWAITING);
            relief.setApprovalType(ApprovalType.RELIEF);
            request.addApproval(relief);
            //direct supervisor who approve the request
            supervisor = new Approval();
            supervisor.setSortedIndex(2);
            supervisor.setId(2L);
            supervisor.setApprovalType(ApprovalType.APPROVE);
            request.addApproval(supervisor);
            //head department who authorize the request
            manager = new Approval();
            manager.setSortedIndex(3);
            manager.setId(3L);
            manager.setApprovalType(ApprovalType.AUTHORIZE);
            request.addApproval(manager);
        } else {
            Long id = Long.parseLong(sid);
            form = leaveFormService.findByRequestId(id);
            TreeSet<Approval> approvals = requestService.findApproval(form.getRequest().getId());
            for (Approval approval : form.getRequest().getApprovals()) {
                if (approval.getApprovalType() == ApprovalType.RELIEF) {
                    relief = approval;
                }
                if (approval.getApprovalType() == ApprovalType.APPROVE) {
                    supervisor = approval;
                }
                if (approval.getApprovalType() == ApprovalType.AUTHORIZE) {
                    manager = approval;
                }
            }
        }
    }
    //endregion

    //region > Component
    @Command
    public void selectRequestFor() {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("employees", employeeService.findPaging(0, StandardFormat.getDefaultPageSize()));
        map.put("totalSize", employeeService.count());
        map.put("receiver", "selectRequestForCallback");
        Window window = (Window) Executions.createComponents(
                "/view/component/employee-selector.zul", null, map);
        window.doModal();
    }

    @GlobalCommand
    public void selectRequestForCallback(@BindingParam("employee") final Employee employee) {
        this.form.getRequest().setRequestFor(employee);
        postNotifyChange(form.getRequest(), "requestFor");
    }

    @GlobalCommand
    public void selectRequestForCallbackFilter(
            @BindingParam("windows") Object window,
            @BindingParam("employees")ListModelList<Employee> employees,
            @BindingParam("pageSize") final int pageSize,
            @BindingParam("activePage") final int activePage,
            @BindingParam("filter") final String filter,
            @BindingParam("filterBy") final String filterBy,
            @BindingParam("filters") final HashMap<String, Object> filters) {

        int offset = activePage * pageSize;
        employees.clear();
        employees.addAll(employeeService.findPaging(offset, pageSize, filter, filterBy, filters));
        postNotifyChange(window, "employees");
        ListPagingVM win = (ListPagingVM) window;
        win.setTotalSize(employeeService.count(filter, filterBy, filters));
        postNotifyChange(window, "totalSize");
    }

    @Command
    public void selectRelief() {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("employees", employeeService.findPaging(0, StandardFormat.getDefaultPageSize()));
        map.put("totalSize", employeeService.count());
        map.put("receiver", "selectReliefCallback");
        Window window = (Window) Executions.createComponents(
                "/view/component/employee-selector.zul", null, map);
        window.doModal();
    }

    public Validator getReliefValidator() {
        return new AbstractValidator() {
            public void validate(ValidationContext ctx) {
                if (relief.getApprovePerson() == null) {
                    addInvalidMessage(ctx,"You can't leave this empty.");
                }
            }
        };
    }

    @GlobalCommand
    public void selectReliefCallback(@BindingParam("employee") final Employee employee) {
        this.relief.setApprovePerson(employee);
        postNotifyChange(this,"relief");
    }

    @GlobalCommand
    public void selectReliefCallbackFilter(
            @BindingParam("windows") Object window,
            @BindingParam("employees")ListModelList<Employee> employees,
            @BindingParam("pageSize") final int pageSize,
            @BindingParam("activePage") final int activePage,
            @BindingParam("filter") final String filter,
            @BindingParam("filterBy") final String filterBy,
            @BindingParam("filters") final HashMap<String, Object> filters) {

        int offset = activePage * pageSize;
        employees.clear();
        employees.addAll(employeeService.findPaging(offset, pageSize, filter, filterBy, filters));
        postNotifyChange(window, "employees");
        ListPagingVM win = (ListPagingVM) window;
        win.setTotalSize(employeeService.count(filter, filterBy, filters));
        postNotifyChange(window, "totalSize");
    }

    @Command
    public void selectSupervisor() {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("employees", employeeService.findPaging(0, StandardFormat.getDefaultPageSize()));
        map.put("totalSize", employeeService.count());
        map.put("receiver", "selectSupervisorCallback");
        Window window = (Window) Executions.createComponents(
                "/view/component/employee-selector.zul", null, map);
        window.doModal();
    }

    public Validator getSupervisorValidator() {
        return new AbstractValidator() {
            public void validate(ValidationContext ctx) {
                if (supervisor.getApprovePerson() == null) {
                    addInvalidMessage(ctx,"You can't leave this empty.");
                }
            }
        };
    }

    @GlobalCommand
    public void selectSupervisorCallback(@BindingParam("employee") final Employee employee) {
        this.supervisor.setApprovePerson(employee);
        postNotifyChange(this,"supervisor" );
    }

    @GlobalCommand
    public void selectSupervisorCallbackFilter(
            @BindingParam("windows") Object window,
            @BindingParam("employees")ListModelList<Employee> employees,
            @BindingParam("pageSize") final int pageSize,
            @BindingParam("activePage") final int activePage,
            @BindingParam("filter") final String filter,
            @BindingParam("filterBy") final String filterBy,
            @BindingParam("filters") final HashMap<String, Object> filters) {

        int offset = activePage * pageSize;
        employees.clear();
        employees.addAll(employeeService.findPaging(offset, pageSize, filter, filterBy, filters));
        postNotifyChange(window, "employees");
        ListPagingVM win = (ListPagingVM) window;
        win.setTotalSize(employeeService.count(filter, filterBy, filters));
        postNotifyChange(window, "totalSize");
    }

    @Command
    public void selectManager() {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("employees", employeeService.findPaging(0, StandardFormat.getDefaultPageSize()));
        map.put("totalSize", employeeService.count());
        map.put("receiver", "selectManagerCallback");
        Window window = (Window) Executions.createComponents(
                "/view/component/employee-selector.zul", null, map);
        window.doModal();
    }

    public Validator getManagerValidator() {
        return new AbstractValidator() {
            public void validate(ValidationContext ctx) {
                if (manager.getApprovePerson() == null) {
                    addInvalidMessage(ctx,"You can't leave this empty.");
                }
            }
        };
    }

    @GlobalCommand
    public void selectManagerCallback(@BindingParam("employee") final Employee employee) {
        this.manager.setApprovePerson(employee);
        postNotifyChange(this, "manager" );
    }

    @GlobalCommand
    public void selectManagerCallbackFilter(
            @BindingParam("windows") Object window,
            @BindingParam("employees")ListModelList<Employee> employees,
            @BindingParam("pageSize") final int pageSize,
            @BindingParam("activePage") final int activePage,
            @BindingParam("filter") final String filter,
            @BindingParam("filterBy") final String filterBy,
            @BindingParam("filters") final HashMap<String, Object> filters) {

        int offset = activePage * pageSize;
        employees.clear();
        employees.addAll(employeeService.findPaging(offset, pageSize, filter, filterBy, filters));
        postNotifyChange(window, "employees");
        ListPagingVM win = (ListPagingVM) window;
        win.setTotalSize(employeeService.count(filter, filterBy, filters));
        postNotifyChange(window, "totalSize");
    }

    @Command
    @NotifyChange({"form", "relief", "supervisor", "manager"})
    public void submit() {

        Request request = form.getRequest();
        request.setRequestDate(new Timestamp(new Date().getTime()));
        request.setRequestBy(userCredentialService.getCurrentEmployee());
        request.setStatus(RequestStatus.PENDING);
        request.setDecisionStatus(DecisionStatus.AWAITING);
        violations = validator.validate(form);
        if(violations.size()>0){
            String str = "Information you provide is invalid. Please make sure you input all mandatory (*) fields and try again.";
            Clients.showNotification(str, Clients.NOTIFICATION_TYPE_ERROR, null, "middle_center", 0, false);
            return;
        }
        relief.setId(null);
        supervisor.setId(null);
        manager.setId(null);
        request.getApprovals().clear();
        request = requestService.create(request);
        form.setRequest(request);
        leaveFormService.create(form);
        request.addApproval(relief);
        request.addApproval(supervisor);
        request.addApproval(manager);
        relief = approvalService.create(relief);
        supervisor = approvalService.create(supervisor);
        manager = approvalService.create(manager);
        String str = "Form submitted successfully and waiting for relief to confirm.";
        Clients.showNotification(str, Clients.NOTIFICATION_TYPE_INFO, null, "middle_center", 0, false);
    }
    //endregion

    //region > Command
    @Command
    public void countDays(){
        if(form.getFromDate() == null
                || form.getToDate() == null
                || form.getLeaveType() == null) {
            return;
        }
        double todayDays = form.getLeaveType().getCalculator().calculate(form);
        form.setTotalDays(todayDays);
        postNotifyChange(form, "totalDays");
    }
    //endregion


    //region > Validator
    public Validator getRequestForValidator() {
        return new AbstractValidator() {
            public void validate(ValidationContext ctx) {
                if (form.getRequest().getRequestFor() == null) {
                    addInvalidMessage(ctx, "requestFor","You can't leave this empty.");
                }
            }
        };
    }
    //endregion
}
