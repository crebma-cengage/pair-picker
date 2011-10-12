import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Stuff {
  public static void main(final String[] args) throws IOException {
    new Stuff().run();
  };

  public void run() throws IOException {
    final JFrame frame = new JFrame("stuff");
    final JPanel panel = new JPanel();
    final JLabel label = new JLabel("Pick the pair!");
    panel.add(label);

    final List<MagnusCheckBox> users = new ArrayList<MagnusCheckBox>();

    final ActionListener ambersSass = new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        final StringBuilder displayName = new StringBuilder();
        final StringBuilder email = new StringBuilder();
        for (final MagnusCheckBox magnusCheckBox : users) {
          if (magnusCheckBox.isSelected()) {
            if (displayName.length() > 0) {
              displayName.append('/');
            }
            displayName.append(magnusCheckBox.getDisplayName());
            if (email.length() > 0) {
              email.append('-');
            }
            email.append(magnusCheckBox.getUserId());
          }
        }
        try {
          setTheGitEmail(email.toString() + "@pair.cengage.com");
          setTheGitUser(displayName.toString());
        } catch (final IOException e1) {
          throw new RuntimeException(e1);
        } catch (final InterruptedException e1) {
          throw new RuntimeException(e1);
        }
      }
    };

    final Properties props = new Properties();
    final InputStream resource = getClass().getResourceAsStream("git-users.properties");
    props.load(resource);
    resource.close();

    for (final Entry<Object, Object> entry : props.entrySet()) {
      final MagnusCheckBox checkBox = new MagnusCheckBox(entry.getValue().toString(), entry.getKey().toString());
      panel.add(checkBox);
      checkBox.addActionListener(ambersSass);
      users.add(checkBox);
    }

    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

  protected void setTheGitEmail(final String text) throws IOException, InterruptedException {
    final String[] a = { "git", "config", "--global", "user.email", text };
    final Process exec = Runtime.getRuntime().exec(a);
    exec.waitFor();
  }

  protected void setTheGitUser(final String text) throws IOException, InterruptedException {
    final String[] a = { "git", "config", "--global", "user.name", text };
    final Process exec = Runtime.getRuntime().exec(a);
    exec.waitFor();
  }

  private static final class MagnusCheckBox extends JCheckBox {
    private static final long serialVersionUID = -1188705878485701015L;

    private final String displayName;
    private final String userId;

    public MagnusCheckBox(final String displayName, final String userId) {
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