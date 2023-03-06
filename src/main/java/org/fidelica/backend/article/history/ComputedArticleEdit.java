package org.fidelica.backend.article.history;

public record ComputedArticleEdit(ArticleEdit edit, String oldContent, String newContent) {

}
