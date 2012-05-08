import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class Stuff {
  public static void main(final String[] args) throws IOException {
    new Stuff().run();
  };

  public void run() throws IOException {
    final JPanel panel = new JPanel(new GridBagLayout());
    panel.add(new JLabel("Pick the pair!"), constraints(0));
    makeAllCommitterCheckboxes(panel);
    makeAndShowFrame(panel);
  }

  private void makeAllCommitterCheckboxes(final JPanel panel) throws IOException {
    final List<CommitterRadioButton> users = new ArrayList<CommitterRadioButton>();
    final ActionListener listener = new GitUpdatingListener(users);
    int count = 1;
    for (final Entry<Object, Object> entry : getCommitters().entrySet()) {
      final CommitterRadioButton checkBox = makeCheckbox(listener, entry);
      panel.add(checkBox, constraints(count++));
      users.add(checkBox);
    }
  }

  private CommitterRadioButton makeCheckbox(final ActionListener listener, final Entry<Object, Object> entry) {
    final CommitterRadioButton radioButton = new CommitterRadioButton(entry.getValue().toString(), entry.getKey().toString());
    radioButton.addActionListener(listener);
    return radioButton;
  }

  private void makeAndShowFrame(final JPanel panel) {
    final JFrame frame = new JFrame("Pair Picker!");
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

  private Map<Object, Object> getCommitters() throws IOException {
    final Properties props = new Properties();
    final InputStream resource = getClass().getResourceAsStream("git-users.properties");
    props.load(resource);
    resource.close();
    return new TreeMap<Object, Object>(props);
  }

  private GridBagConstraints constraints(final int y) {
    final GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridx = 0;
    constraints.gridy = y;
    constraints.anchor = GridBagConstraints.WEST;
    return constraints;
  }

  private static final class GitUpdatingListener implements ActionListener {
    private final List<CommitterRadioButton> users;

    private GitUpdatingListener(final List<CommitterRadioButton> users) {
      this.users = users;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
      final StringBuilder displayName = new StringBuilder();
      final StringBuilder email = new StringBuilder();

      for (final CommitterRadioButton magnusCheckBox : users) {
        if (magnusCheckBox.isSelected()) {
          addDisplayName(displayName, magnusCheckBox);
          addEmail(email, magnusCheckBox);
        }
      }

      setGitUserAndEmail(displayName, email);
    }

    private void setGitUserAndEmail(final StringBuilder displayName, final StringBuilder email) {
      try {
        setTheGitEmail(email.toString() + "@pair.cengage.com");
        setTheGitUser(displayName.toString());
      } catch (final Exception booty) {
      }
    }

    private void addEmail(final StringBuilder email, final CommitterRadioButton checkBox) {
      if (email.length() > 0) {
        email.append('-');
      }
      email.append(checkBox.getUserId());
    }

    private void addDisplayName(final StringBuilder displayName, final CommitterRadioButton magnusCheckBox) {
      if (displayName.length() > 0) {
        displayName.append('/');
      }
      displayName.append(magnusCheckBox.getDisplayName());
    }

    private void setTheGitEmail(final String text) throws IOException, InterruptedException {
      final String[] a = { "git", "config", "--global", "user.email", text };
      final Process exec = Runtime.getRuntime().exec(a);
      exec.waitFor();
    }

    private void setTheGitUser(final String text) throws IOException, InterruptedException {
      final String[] a = { "git", "config", "--global", "user.name", text };
      final Process exec = Runtime.getRuntime().exec(a);
      exec.waitFor();
    }
  }

  private static final class CommitterRadioButton extends JRadioButton {
    private static final long serialVersionUID = -1188705878485701015L;

    private final String displayName;
    private final String userId;

    public CommitterRadioButton(final String displayName, final String userId) {
      super(displayName);
      this.displayName = displayName;
      this.userId = userId;
    }

    public String getDisplayName() {
      return displayName;
    }

    public String getUserId() {
      return userId;
    }

  }
}
