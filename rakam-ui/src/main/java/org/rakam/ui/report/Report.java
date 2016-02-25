package org.rakam.ui.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.rakam.plugin.ProjectItem;
import org.rakam.server.http.annotations.ApiParam;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


public class Report implements ProjectItem {
    public final String project;
    public final String slug;
    public final String category;
    public final String name;
    public final String query;
    public final boolean shared;
    public final Map<String, Object> options;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Boolean hasPermission;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer userId;

    @JsonCreator
    public Report(@ApiParam(name = "project", required = true) String project,
                  @ApiParam(name = "slug", value="Short name of the report") String slug,
                  @ApiParam(name = "category", value="Category of the report", required = false) String category,
                  @ApiParam(name = "name", value="The name of the report") String name,
                  @ApiParam(name = "query", value="The sql query that will be executed") @JsonProperty("query") String query,
                  @ApiParam(name = "options", value="Additional information about the materialized view", required = false) Map<String, Object> options,
                  @ApiParam(name = "shared", value="Shared with other users") boolean shared)
    {
        this.project = checkNotNull(project, "project is required");
        this.name = checkNotNull(name, "name is required");
        this.slug = checkNotNull(slug, "slug is required");
        this.query = checkNotNull(query, "query is required");
        this.options = options;
        this.shared = shared;
        this.category = category;

        checkArgument(this.slug.matches("^[A-Za-z]+[A-Za-z0-9_]*"),
                "slug must only contain alphanumeric characters and _");
    }

    public void setPermission(boolean hasPermission) {
        this.hasPermission = hasPermission;
    }

    @Override
    public String project() {
        return project;
    }

    public void setUserId(int user) {
        this.userId = user;
    }
}

