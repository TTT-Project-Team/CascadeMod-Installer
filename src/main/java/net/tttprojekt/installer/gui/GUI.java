package net.tttprojekt.installer.gui;

import lombok.Getter;
import lombok.Setter;
import net.tttprojekt.installer.Main;
import net.tttprojekt.installer.download.Download;
import net.tttprojekt.installer.installer.IForgeInstaller;
import net.tttprojekt.installer.installer.IModInstaller;
import net.tttprojekt.installer.utils.CascadeDownloadFetcher;
import net.tttprojekt.installer.utils.MinecraftChecker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI {

    private static final Font APPLICATION_FONT = new Font("Roboto", Font.PLAIN, 14);
    private static final ImageIcon ICON_IMAGE;
    private static final ImageIcon BANNER_IMAGE;

    static {
        ICON_IMAGE = new ImageIcon(Main.class.getClassLoader().getResource("installer-icon.png"));
        ImageIcon rawBanner = new ImageIcon(Main.class.getClassLoader().getResource("tttproject-banner.png"));

        Image image = rawBanner.getImage();
        float scale = 0.06f;
        int newWidth = (int) (rawBanner.getIconWidth() * scale);
        int newHeight = (int) (rawBanner.getIconHeight() * scale);
        BANNER_IMAGE = new ImageIcon(image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH));

    }

    private final String title;
    private final int width;
    private final int height;


    private JFrame mainFrame;
    private GridBagConstraints bagConstraints;

    private JPanel logoBannerPanel;
    private JPanel checkBoxPanel;
    private JPanel downloadLabelPanel;
    private JPanel authorLabelPanel;
    private JPanel buttonPanel;

    private JCheckBox checkBoxCreateBackup;
    private JCheckBox checkBoxDownloadOptiFine;
    private JCheckBox checkBoxDownloadJustEnoughItems;
//    private JCheckBox checkBoxDownloadToggleSprint;

    private JLabel labelDownload;
    private JLabel labelAuthor;

    private JButton buttonDownloadMods;
    private JButton buttonDownloadForge;

    private final Runnable windowClose;

    @Setter private IModInstaller modInstaller;
    @Setter private IForgeInstaller forgeInstaller;

    public GUI(String title, int width, int height, Runnable windowClose) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        this.windowClose = windowClose;

        this.title = title;
        this.width = width;
        this.height = height;
        createFrame();
        createComponents();

        changeFont(this.mainFrame);

        this.mainFrame.pack();
        this.mainFrame.setVisible(true);
    }

    private void createFrame() {
        this.mainFrame = new JFrame(this.title);
        this.mainFrame.setSize(this.width, this.height);
        this.mainFrame.setResizable(false);
        this.mainFrame.setLocationRelativeTo(null);
        this.mainFrame.setLayout(new GridBagLayout());
        this.mainFrame.setIconImage(ICON_IMAGE.getImage());

        this.mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                windowClose.run();
            }
        });

        bagConstraints = new GridBagConstraints();

        this.mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void createComponents() {
        createLogoBanner();
        createCheckBoxes();
        createDownloadLabel();
        createAuthorLabel();
        createButtons();
    }

    private void createLogoBanner() {

        this.logoBannerPanel = new JPanel();
        this.logoBannerPanel.setLayout(new BoxLayout(this.logoBannerPanel, BoxLayout.Y_AXIS));


        this.bagConstraints.gridx = 0;
        this.bagConstraints.gridy = 0;
        this.bagConstraints.insets = new Insets(0, 50, 0, 50);
        this.bagConstraints.weightx = 1;

        this.mainFrame.add(this.logoBannerPanel, this.bagConstraints);

        JLabel logoBanner = new JLabel(BANNER_IMAGE);

        this.logoBannerPanel.add(logoBanner);
    }

    private void createCheckBoxes() {
        this.checkBoxPanel = new JPanel();
        this.checkBoxPanel.setLayout(new BoxLayout(this.checkBoxPanel, BoxLayout.Y_AXIS));


        this.bagConstraints.gridx = 0;
        this.bagConstraints.gridy = 1;
        this.bagConstraints.insets = new Insets(0, 50, 20, 50);
        this.bagConstraints.weightx = 1;

        this.mainFrame.add(this.checkBoxPanel, this.bagConstraints);

        this.checkBoxCreateBackup = createCheckBox("Create Backup", "Create a backup folder before downloading mods.", itemEvent -> {
            if (this.modInstaller != null) {
                this.modInstaller.setCreateBackup(itemEvent.getStateChange() == ItemEvent.SELECTED);
            }
        });

        this.checkBoxDownloadOptiFine = createCheckBox("Download OptiFine", "When enabled OptiFine will be downloaded.", itemEvent -> {
            if (this.modInstaller != null) {
                this.modInstaller.setDownloadOptiFine(itemEvent.getStateChange() == ItemEvent.SELECTED);
            }
        });

        this.checkBoxDownloadJustEnoughItems = createCheckBox("Download JustEnoughItems (JEI)", "When enabled JEI will be downloaded.", itemEvent -> {
            if (this.modInstaller != null) {
                this.modInstaller.setDownloadJustEnoughItems(itemEvent.getStateChange() == ItemEvent.SELECTED);
            }
        });

//        this.checkBoxDownloadToggleSprint = createCheckBox("Download ToggleSprint", "When enabled ToggleSprint will be downloaded.", itemEvent -> {
//            if (this.modInstaller != null) {
//                this.modInstaller.setDownloadToggleSprint(itemEvent.getStateChange() == ItemEvent.SELECTED);
//            }
//        });

        this.checkBoxPanel.add(this.checkBoxCreateBackup);
        this.checkBoxPanel.add(this.checkBoxDownloadOptiFine);
        this.checkBoxPanel.add(this.checkBoxDownloadJustEnoughItems);
//        this.checkBoxPanel.add(this.checkBoxDownloadToggleSprint);
    }

    private void createDownloadLabel() {
        this.downloadLabelPanel = new JPanel();
        this.downloadLabelPanel.setLayout(new BoxLayout(this.downloadLabelPanel, BoxLayout.Y_AXIS));

        this.bagConstraints.gridx = 0;
        this.bagConstraints.gridy = 2;
        this.bagConstraints.insets = new Insets(0, 50, 10, 50);
        this.bagConstraints.weightx = 1;

        this.mainFrame.add(this.downloadLabelPanel, this.bagConstraints);

        this.labelDownload = new JLabel("Downloading...");
        this.labelDownload.setForeground(new Color(159, 37, 37));

        this.downloadLabelPanel.add(this.labelDownload);
    }

    private void createAuthorLabel() {
        this.authorLabelPanel = new JPanel();
        this.authorLabelPanel.setLayout(new BoxLayout(this.authorLabelPanel, BoxLayout.Y_AXIS));

        this.bagConstraints.gridx = 1;
        this.bagConstraints.gridy = 5;
        this.bagConstraints.insets = new Insets(0, -150, 5, 0);
        this.bagConstraints.weightx = 1;

        this.mainFrame.add(this.authorLabelPanel, this.bagConstraints);

        this.labelAuthor = new JLabel("Developed by Nico");
        this.labelAuthor.setForeground(new Color(40, 40, 40));

        this.authorLabelPanel.add(this.labelAuthor);

    }

    private void createButtons() {
        this.buttonPanel = new JPanel();
        this.buttonPanel.setLayout(new BoxLayout(this.buttonPanel, BoxLayout.X_AXIS));

        this.bagConstraints.gridx = 0;
        this.bagConstraints.gridy = 3;
        this.bagConstraints.ipady = 5;
        this.bagConstraints.insets = new Insets(0, 50, 25, 50);
        this.bagConstraints.weightx = 1;

        this.mainFrame.add(this.buttonPanel, this.bagConstraints);

        this.buttonDownloadMods = createButton("Download Mods", action -> {
            if (checkMinecraftRunning()) {
                return;
            }
            onButtonDownloadModsClicked();
        });
        this.buttonDownloadForge = createButton("Install Forge", action -> {
            if (checkMinecraftRunning()) {
                return;
            }
            onButtonDownloadForgeClicked();
        });

        this.buttonPanel.add(this.buttonDownloadMods);
        this.buttonPanel.add(this.buttonDownloadForge);
    }

    public void updateCheckBox() {
        this.checkBoxCreateBackup.setSelected(true);
        this.checkBoxDownloadOptiFine.setSelected(true);
        this.checkBoxDownloadJustEnoughItems.setSelected(true);
        //this.checkBoxDownloadToggleSprint.setSelected(true);
    }

    private void toggleElements(boolean enabled) {
        this.checkBoxCreateBackup.setEnabled(enabled);
        this.checkBoxDownloadOptiFine.setEnabled(enabled);
        this.checkBoxDownloadJustEnoughItems.setEnabled(enabled);
        //this.checkBoxDownloadToggleSprint.setEnabled(enabled);

        this.buttonDownloadForge.setEnabled(enabled);
        this.buttonDownloadMods.setEnabled(enabled);
    }

    private void setStatusLabel(String text, LabelStatus status) {
        this.labelDownload.setForeground(status.getColor());
        this.labelDownload.setText(text);
    }

    private void clearStatusLabel() {
        setStatusLabel("", LabelStatus.NEUTRAL);
    }

    public void loading(boolean loading) {
        this.checkBoxCreateBackup.setVisible(!loading);
        this.checkBoxDownloadOptiFine.setVisible(!loading);
        this.checkBoxDownloadJustEnoughItems.setVisible(!loading);
        //this.checkBoxDownloadToggleSprint.setVisible(!loading);

        this.buttonDownloadForge.setVisible(!loading);
        this.buttonDownloadMods.setVisible(!loading);

        this.labelAuthor.setVisible(!loading);

        if (loading) {
            setStatusLabel("Loading downloader...", LabelStatus.WORKING);
        } else {
            clearStatusLabel();
        }

    }

    private JCheckBox createCheckBox(String title, String tooltip, ItemListener itemListener) {
        JCheckBox checkBox = new JCheckBox(title);
        checkBox.setToolTipText(tooltip);
        checkBox.setFocusPainted(false);
        checkBox.addItemListener(itemListener);

        return checkBox;
    }

    private void onButtonDownloadModsClicked() {
        if (this.modInstaller == null) return;
        toggleElements(false);
        setStatusLabel("Downloading mods...", LabelStatus.WORKING);

        Download.CASCADE_MOD.updateURL(CascadeDownloadFetcher.getLatestVersion());

        SwingUtilities.invokeLater(() -> {
            this.modInstaller.backupModFolder();
            this.modInstaller.createModFolder();
            this.modInstaller.downloadMods(() -> {
                setStatusLabel("Mods downloaded!", LabelStatus.SUCCESS);
                toggleElements(true);
            });
        });


    }

    private void onButtonDownloadForgeClicked() {
        if (this.forgeInstaller == null) return;
        toggleElements(false);
        setStatusLabel("Downloading forge...", LabelStatus.WORKING);

        SwingUtilities.invokeLater(() -> {
            this.forgeInstaller.setup();
            this.forgeInstaller.download();

            setStatusLabel("Installing forge...", LabelStatus.WORKING);
            SwingUtilities.invokeLater(() -> {
                if (this.forgeInstaller.install()) {
                    setStatusLabel("Successfully installed forge.", LabelStatus.SUCCESS);
                } else {
                    setStatusLabel("Failed to install forge.", LabelStatus.FAIL);
                }
                this.forgeInstaller.cleanUp();
                toggleElements(true);
            });
        });
    }

    private JButton createButton(String text, ActionListener actionListener) {
        JButton jButton = new JButton(text);
        jButton.setFocusPainted(false);
        jButton.addActionListener(actionListener);

        return jButton;
    }

    private boolean checkMinecraftRunning() {
        if (!MinecraftChecker.isMinecraftRunning()) return false;
        JLabel messageLabel = new JLabel("<html>Minecraft has been detected.<br/>Please close it before starting a download.</html>");
        messageLabel.setFont(new Font(APPLICATION_FONT.getName(), Font.PLAIN, APPLICATION_FONT.getSize() - 1));
        JOptionPane.showMessageDialog(this.mainFrame, messageLabel,
                "Minecraft Detection",
                JOptionPane.ERROR_MESSAGE);
        return true;
    }

    private void changeFont(Component component) {
        component.setFont(APPLICATION_FONT);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                changeFont(child);
            }
        }
    }

    enum LabelStatus {

        SUCCESS(new Color(48, 245, 88)),
        FAIL(new Color(245, 93, 48)),
        WORKING(new Color(245, 195, 48)),
        NEUTRAL(Color.BLACK);

        @Getter private final Color color;

        LabelStatus(Color color) {
            this.color = color;
        }

    }

}
