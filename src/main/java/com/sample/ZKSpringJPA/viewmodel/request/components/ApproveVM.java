package com.sample.ZKSpringJPA.viewmodel.request.components;


import com.sample.ZKSpringJPA.entity.request.DecisionStatus;
import com.sample.ZKSpringJPA.entity.request.Request;
import com.sample.ZKSpringJPA.entity.request.RequestStatus;
import com.sample.ZKSpringJPA.entity.request.approval.Approval;
import com.sample.ZKSpringJPA.services.request.ApprovalService;
import com.sample.ZKSpringJPA.services.request.RequestService;
import com.sample.ZKSpringJPA.utils.UserCredentialService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;

import java.sql.Timestamp;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ApproveVM extends ApprovalVM {
    //region > Inject Service
    @WireVariable
    private UserCredentialService userCredentialService;

    @WireVariable
    private ApprovalService approvalService;

    @WireVariable
    private RequestService requestService;
    //endregion

    //region > Fields

    public boolean isApproveAble(){
        if(userCredentialService.getCurrentEmployee()==null || getApproval().getApprovePerson()==null){
            return false;
        }
        boolean approvAble =  userCredentialService.getCurrentEmployee().getId().equals(getApproval().getApprovePerson().getId());
        return (! isApproved()) && approvAble && this.getApproval().getDecisionStatus() == DecisionStatus.AWAITING;
    }

    public boolean isApproved(){
        return !(this.getApproval().getDecisionStatus() == null
                || this.getApproval().getDecisionStatus() == DecisionStatus.AWAITING);
    }

    //endregion

    //region > Constructor

    @Init
    public void init(@ContextParam(ContextType.VIEW) Component view,
                     @ExecutionArgParam("componentModel") Approval approval) {
        Selectors.wireComponents(view, this, false);
        setApproval(approval);

    }

    //endregion

    //region > Command
    @Command
    @NotifyChange({"approval", "approveAble", "approved"})
    public void approve(@BindingParam("request") final Request request){
        getApproval().setDecisionStatus(DecisionStatus.APPROVED);
        getApproval().getRequest().setStatus(RequestStatus.OPEN);
        getApproval().getRequest().setDecisionStatus(DecisionStatus.AWAITING);
        getApproval().setApproveDate(new Timestamp(System.currentTimeMillis()));
        setApproval(approvalService.update(getApproval()));
        for (Approval a: request.getApprovals()) {
            if(a.getSortedIndex() == getApproval().getSortedIndex()+1){
                a.setDecisionStatus(DecisionStatus.AWAITING);
                approvalService.update(a);
                break;
            }
        }
        requestService.update(request);
        postNotifyChange(request, "status");
        String str = "Request successfully authorize";
        Clients.showNotification(str, Clients.NOTIFICATION_TYPE_INFO, null, "middle_center", 0, false);
    }
    @Command
    @NotifyChange({"approval", "approveAble", "approved"})
    public void reject(@BindingParam("request") final Request request){
        if(StringUtils.isEmpty(getApproval().getComment())){
            String str = "Please give brief comment of rejection.";
            Clients.showNotification(str, Clients.NOTIFICATION_TYPE_ERROR, null, "middle_center", 0, false);
            return;
        }
        getApproval().setDecisionStatus(DecisionStatus.REJECTED);
        getApproval().getRequest().setStatus(RequestStatus.CLOSED);
        getApproval().getRequest().setDecisionStatus(DecisionStatus.REJECTED);
        getApproval().setApproveDate(new Timestamp(System.currentTimeMillis()));
        setApproval(approvalService.update(getApproval()));
        requestService.update(getApproval().getRequest());
        postNotifyChange(request, "status");
        String str = "Request successfully rejected";
        Clients.showNotification(str, Clients.NOTIFICATION_TYPE_ERROR, null, "middle_center", 0, false);
    }
    //endregion
}
