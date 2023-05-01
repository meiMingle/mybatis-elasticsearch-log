package cloud.pandas.plugin.mybatis.setting;

import com.alibaba.druid.DbType;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ResourceBundle;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;

public class MybatisLogSettingsDialog {
    private JPanel myMainPanel;
    private JCheckBox myFormat;
    private JComboBox<String> myDbTypes;
    private JSpinner myPort;

    public MybatisLogSettingsDialog() {
        this.$$$setupUI$$$();
        for (DbType dbType : DbType.values()) {
            this.myDbTypes.addItem(dbType.name());
        }
        this.myFormat.addChangeListener(e -> {
            JToggleButton checkBox = (JToggleButton)e.getSource();
            this.myDbTypes.setEnabled(checkBox.isSelected());
        });
    }

    public JPanel getPanel() {
        return this.myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return this.myFormat;
    }

    public boolean getFormat() {
        return this.myFormat.isSelected();
    }

    public void setFormat(boolean newStatus) {
        this.myFormat.setSelected(newStatus);
    }

    public String getDbType() {
        return (String)this.myDbTypes.getSelectedItem();
    }

    public void setDbType(String dbType) {
        this.myDbTypes.setSelectedItem(dbType);
    }

    public int getPort() {
        int port = (Integer)this.myPort.getValue();
        if (port <= 0 || port >= 65535) {
            return 5866;
        }
        return port;
    }

    public void setPort(int port) {
        this.myPort.setValue(port);
    }

    private void $$$setupUI$$$() {
        JCheckBox jCheckBox;
        JSpinner jSpinner;
        JPanel jPanel;
        this.myMainPanel = jPanel = new JPanel();
        jPanel.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jPanel.setMaximumSize(new Dimension(400, 200));
        jPanel.setMinimumSize(new Dimension(215, 178));
        JLabel jLabel = new JLabel();
        this.$$$loadLabelText$$$(jLabel, ResourceBundle.getBundle("18bin").getString("format.sql"));
        jPanel.add((Component)jLabel, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, null, null, null));
        Spacer spacer = new Spacer();
        jPanel.add((Component)spacer, new GridConstraints(0, 2, 1, 1, 0, 1, 6, 1, null, null, null));
        JComboBox jComboBox = new JComboBox();
        this.myDbTypes = jComboBox;
        jPanel.add(jComboBox, new GridConstraints(1, 1, 1, 1, 8, 1, 0, 0, null, null, null));
        JLabel jLabel2 = new JLabel();
        this.$$$loadLabelText$$$(jLabel2, ResourceBundle.getBundle("18bin").getString("http.port"));
        jPanel.add((Component)jLabel2, new GridConstraints(2, 0, 1, 1, 8, 0, 0, 0, null, null, null));
        Spacer spacer2 = new Spacer();
        jPanel.add((Component)spacer2, new GridConstraints(3, 0, 1, 1, 0, 2, 1, 6, null, null, null));
        this.myPort = jSpinner = new JSpinner();
        jPanel.add((Component)jSpinner, new GridConstraints(2, 1, 1, 1, 8, 1, 0, 0, null, new Dimension(50, -1), null));
        Spacer spacer3 = new Spacer();
        jPanel.add((Component)spacer3, new GridConstraints(3, 1, 1, 1, 0, 2, 1, 6, null, null, null));
        Spacer spacer4 = new Spacer();
        jPanel.add((Component)spacer4, new GridConstraints(1, 2, 1, 1, 0, 1, 6, 1, null, null, null));
        Spacer spacer5 = new Spacer();
        jPanel.add((Component)spacer5, new GridConstraints(2, 2, 1, 1, 0, 1, 6, 1, null, null, null));
        this.myFormat = jCheckBox = new JCheckBox();
        jCheckBox.setText("");
        jPanel.add((Component)jCheckBox, new GridConstraints(0, 1, 1, 1, 8, 0, 3, 0, null, null, null));
        jLabel.setLabelFor(jComboBox);
        jLabel2.setLabelFor(jSpinner);
    }

    public JComponent $$$getRootComponent$$$() {
        return this.myMainPanel;
    }

    private void $$$loadLabelText$$$(JLabel jLabel, String string) {
        StringBuffer stringBuffer = new StringBuffer();
        boolean bl = false;
        char c = '\u0000';
        int n = -1;
        for (int i = 0; i < string.length(); ++i) {
            if (string.charAt(i) == '&') {
                if (++i == string.length()) break;
                if (!bl && string.charAt(i) != '&') {
                    bl = true;
                    c = string.charAt(i);
                    n = stringBuffer.length();
                }
            }
            stringBuffer.append(string.charAt(i));
        }
        jLabel.setText(stringBuffer.toString());
        if (bl) {
            jLabel.setDisplayedMnemonic(c);
            jLabel.setDisplayedMnemonicIndex(n);
        }
    }
}
