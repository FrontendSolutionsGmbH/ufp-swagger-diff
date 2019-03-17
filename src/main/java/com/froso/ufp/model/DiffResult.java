package com.froso.ufp.model;

import com.deepoove.swagger.diff.*;
import com.fasterxml.jackson.databind.*;
import java.util.*;

public class DiffResult {

    private SwaggerDiff diff;
    private Map<String, Object> diffMap;
    private String name;
    private int newEndpointsCount = 0;
    private int missingEndpointsCount = 0;
    private int changedEndpointsCount = 0;
    private DiffType semverDiffType;
    private List<SwaggerDiffType> realisedDiffTypes;
    private List<SwaggerDiffType> expectedDiffTypes;
    private List<DiffRemark> remarks;

    public List<DiffRemark> getRemarks() {
        return remarks;
    }

    public void setRemarks(List<DiffRemark> remarks) {
        this.remarks = remarks;
    }

    public DiffType getSemverDiffType() {
        return semverDiffType;
    }

    public void setSemverDiffType(DiffType semverDiffType) {
        this.semverDiffType = semverDiffType;
    }

    public List<SwaggerDiffType> getRealisedDiffTypes() {
        return realisedDiffTypes;
    }

    public void setRealisedDiffTypes(List<SwaggerDiffType> realisedDiffTypes) {
        this.realisedDiffTypes = realisedDiffTypes;
    }

    public List<SwaggerDiffType> getExpectedDiffTypes() {
        return expectedDiffTypes;
    }

    public void setExpectedDiffTypes(List<SwaggerDiffType> expectedDiffTypes) {
        this.expectedDiffTypes = expectedDiffTypes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getDiffMap() {
        return diffMap;
    }

    public void setDiffMap(Map<String, Object> diffMap) {
        this.diffMap = diffMap;
    }

    public int getNewEndpointsCount() {
        return newEndpointsCount;
    }

    public void setNewEndpointsCount(int newEndpointsCount) {
        this.newEndpointsCount = newEndpointsCount;
    }

    public int getMissingEndpointsCount() {
        return missingEndpointsCount;
    }

    public void setMissingEndpointsCount(int missingEndpointsCount) {
        this.missingEndpointsCount = missingEndpointsCount;
    }

    public int getChangedEndpointsCount() {
        return changedEndpointsCount;
    }

    public void setChangedEndpointsCount(int changedEndpointsCount) {
        this.changedEndpointsCount = changedEndpointsCount;
    }

    public SwaggerDiff getDiff() {
        return diff;
    }

    public void setDiff(SwaggerDiff diff) {
        this.diff = diff;

        ObjectMapper oMapper = new ObjectMapper();
        this.diffMap = oMapper.convertValue(diff, Map.class);

        this.setChangedEndpointsCount(diff.getChangedEndpoints().size());
        this.setNewEndpointsCount(diff.getNewEndpoints().size());
        this.setMissingEndpointsCount(diff.getMissingEndpoints().size());
    }


}
