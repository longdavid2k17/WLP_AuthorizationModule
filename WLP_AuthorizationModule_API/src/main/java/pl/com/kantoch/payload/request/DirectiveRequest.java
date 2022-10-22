package pl.com.kantoch.payload.request;

public class DirectiveRequest {
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private Boolean isEnabled;
    private OperationContextEnum operationContextEnum;
    private RequestContextEnum requestContext;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public OperationContextEnum getOperationContext() {
        return operationContextEnum;
    }

    public void setOperationContext(OperationContextEnum operationContextEnum) {
        this.operationContextEnum = operationContextEnum;
    }

    public RequestContextEnum getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(RequestContextEnum requestContext) {
        this.requestContext = requestContext;
    }
}
