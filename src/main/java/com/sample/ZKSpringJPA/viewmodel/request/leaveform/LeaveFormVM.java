package com.sample.ZKSpringJPA.viewmodel.request.leaveform;

import com.sample.ZKSpringJPA.anotation.Feature;
import com.sample.ZKSpringJPA.entity.employment.Employee;
import com.sample.ZKSpringJPA.entity.request.FormType;
import com.sample.ZKSpringJPA.entity.request.Request;
import com.sample.ZKSpringJPA.entity.request.RequestPriority;
import com.sample.ZKSpringJPA.entity.request.RequestStatus;
import com.sample.ZKSpringJPA.entity.request.leaveform.LeaveForm;
import com.sample.ZKSpringJPA.entity.request.leaveform.LeaveType;
import com.sample.ZKSpringJPA.services.employment.EmployeeService;
import com.sample.ZKSpringJPA.services.request.LeaveFormService;
import com.sample.ZKSpringJPA.services.request.RequestService;
import com.sample.ZKSpringJPA.utils.StandardFormat;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Window;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Date;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
@Feature(
        view = "/view/request/forms/leave-form.zul",
        uuid = "leave-form",
        menuOrder = "3.0.1",
        displayName = "Leave Request",
        menuIcon = "tag"
)
public class LeaveFormVM {
    //region > Inject Services
    @WireVariable
    private EmployeeService employeeService;

    @WireVariable
    private RequestService requestService;

    @WireVariable
    private LeaveFormService leaveFormService;
    //endregion

    //region > Fields
    @Setter @Getter
    private LeaveForm form;

    @Getter @Setter
    private Employee requestFor;

    @Getter @Setter
    private Employee relief;

    @Getter @Setter
    private Employee supervisor;

    @Getter @Setter
    private Employee manager;

    @Getter @Setter
    private List<LeaveType> leaveTypes = new ListModelList<>(LeaveType.values());

    @Getter @Setter
    private List<RequestPriority> requestPriorities = new ListModelList<>(RequestPriority.values());

    @Getter
    private final String standardDateTimeFormat = StandardFormat.getStandardDateTimeFormat();
    //endregion

    //region > Constructor
    @Init
    public void init(){
        String sid = Executions.getCurrent().getParameter("id");
        if(sid==null) {
            form = new LeaveForm();
            form.setRequest(new Request());
            form.getRequest().setFormType(FormType.LEAVE_REQUEST);
        } else {
            Long id = Long.parseLong(sid);
            form = leaveFormService.findByRequestId(id);
        }
    }
    //endregion

    //region > Component
    @Command
    public void selectRequestFor(){
        final HashMap<String, Object> map = new HashMap<>();
        map.put("employees", employeeService.findAll());
        map.put("receiver", "selectRequestForCallback");
        Window window = (Window) Executions.createComponents(
                "/view/component/employee-selector.zul", null, map);
        window.doModal();
    }
    @GlobalCommand
    public void selectRequestForCallback(@BindingParam("employee") final Employee employee){
        this.form.getRequest().setRequestFor(employee);
        postNotifyChange("form");
    }

    @Command
    public void selectRelief(){
        final HashMap<String, Object> map = new HashMap<>();
        map.put("employees", employeeService.findAll());
        map.put("receiver", "selectReliefCallback");
        Window window = (Window) Executions.createComponents(
                "/view/component/employee-selector.zul", null, map);
        window.doModal();
    }
    @GlobalCommand
    public void selectReliefCallback(@BindingParam("employee") final Employee employee){
        this.relief = employee;
        postNotifyChange("relief");
    }

    @Command
    public void selectSupervisor(){
        final HashMap<String, Object> map = new HashMap<>();
        map.put("employees", employeeService.findAll());
        map.put("receiver", "selectSupervisorCallback");
        Window window = (Window) Executions.createComponents(
                "/view/component/employee-selector.zul", null, map);
        window.doModal();
    }
    @GlobalCommand
    public void selectSupervisorCallback(@BindingParam("employee") final Employee employee){
        this.supervisor = employee;
        postNotifyChange("supervisor");
    }

    @Command
    public void selectManager(){
        final HashMap<String, Object> map = new HashMap<>();
        map.put("employees", employeeService.findAll());
        map.put("receiver", "selectManagerCallback");
        Window window = (Window) Executions.createComponents(
                "/view/component/employee-selector.zul", null, map);
        window.doModal();
    }
    @GlobalCommand
    public void selectManagerCallback(@BindingParam("employee") final Employee employee){
        this.manager = employee;
        postNotifyChange("manager");
    }

    @Command
    public void submit(){
        Request request = form.getRequest();
        request.setRequestDate(new Timestamp(new Date().getTime()));
        request.setRequestBy(request.getRequestFor());
        request.setStatus(RequestStatus.PENDING);
        request = requestService.create(request);
        leaveFormService.create(form);
    }
    //endregion

    //region > Programmatic
    private void postNotifyChange(final String property){
        BindUtils.postNotifyChange(null,null,this,property);
    }
    //endregion
}
