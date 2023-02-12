package org.fidelica.backend.factcheck.history;

public record ComputedFactCheckEdit(FactCheckEdit edit, String oldContent, String newContent) {

}
