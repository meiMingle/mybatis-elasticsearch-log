package cloud.pandas.plugin.mybatis.action;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.intellij.codeInsight.editorActions.TextBlockTransferable;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import java.awt.datatransfer.Transferable;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class GrepSqlConsoleAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            Editor editor = (Editor)e.getDataContext().getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE);
            if (editor != null) {
                Notification notification = new Notification("", "Grep SQL", "No lines was selected", NotificationType.WARNING);
                SelectionModel selectionModel = editor.getSelectionModel();
                String selectedText = selectionModel.getSelectedText();
                if (StringUtils.isEmpty(selectedText)) {
                    notification.notify(project);
                    return;
                }

                VisualPosition startPosition = selectionModel.getSelectionStartPosition();
                VisualPosition endPosition = selectionModel.getSelectionEndPosition();
                if (startPosition == null || endPosition == null) {
                    notification.notify(project);
                    return;
                }

                String linesText = this.getLinesText(editor, startPosition, endPosition);
                String[] lines = StringUtils.split(linesText, System.lineSeparator());
                List<String> sqlList = SqlFormatter.extraSql(Arrays.asList(lines));
                if (!CollectionUtils.isEmpty(sqlList)) {
                    String sql = Joiner.on(System.lineSeparator()).join(sqlList);
                    Transferable transferable = new TextBlockTransferable(sql, Lists.newArrayList(), null);
                    CopyPasteManager.getInstance().setContents(transferable);
                    new Notification("", "Grep SQL", sql, NotificationType.INFORMATION).notify(project);
                } else {
                    notification.setContent("Found no MyBatis SQL");
                    notification.notify(project);
                }
            }
        }
    }

    private String getLinesText(Editor editor, VisualPosition startPos, VisualPosition endPos) {
        Pair<LogicalPosition, LogicalPosition> lines = EditorUtil.calcSurroundingRange(editor, startPos, endPos);
        LogicalPosition lineStart = (LogicalPosition)lines.first;
        LogicalPosition nextLineStart = (LogicalPosition)lines.second;
        int start = editor.logicalPositionToOffset(lineStart);
        int end = editor.logicalPositionToOffset(nextLineStart);
        return editor.getDocument().getText(new TextRange(start, end));
    }
}
