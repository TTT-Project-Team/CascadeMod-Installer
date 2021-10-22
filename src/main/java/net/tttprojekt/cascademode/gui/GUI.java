package net.tttprojekt.cascademode.gui;

import lombok.Setter;
import net.tttprojekt.cascademode.Main;
import net.tttprojekt.cascademode.installer.IForgeInstaller;
import net.tttprojekt.cascademode.installer.IModInstaller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI {

    private final String title;
    private final int width;
    private final int height;

    private final ImageIcon imageIcon;

    private JFrame mainFrame;
    private GridBagConstraints bagConstraints;

    private JPanel checkBoxPanel;
    private JPanel buttonPanel;

    private JCheckBox checkBoxCreateBackup;
    private JCheckBox checkBoxDownloadOptiFine;
    private JCheckBox checkBoxDownloadJustEnoughItems;

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
        this.imageIcon = new ImageIcon(Main.class.getClassLoader().getResource("installer-icon.png"));
        this.windowClose = windowClose;

        this.title = title;
        this.width = width;
        this.height = height;
        createFrame();
        createComponents();

        this.mainFrame.pack();
        this.mainFrame.setVisible(true);
    }

    private void createFrame() {
        this.mainFrame = new JFrame(this.title);
        this.mainFrame.setSize(this.width, this.height);
        this.mainFrame.setResizable(false);
        this.mainFrame.setLocationRelativeTo(null);
        this.mainFrame.setLayout(new GridBagLayout());
        this.mainFrame.setIconImage(this.imageIcon.getImage());

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
        createCheckBoxes();
        createButtons();
    }

    private void createCheckBoxes() {
        this.checkBoxPanel = new JPanel();
        this.checkBoxPanel.setLayout(new BoxLayout(this.checkBoxPanel, BoxLayout.Y_AXIS));


        this.bagConstraints.gridx = 0;
        this.bagConstraints.gridy = 0;
        this.bagConstraints.insets = new Insets(20, 50, 20, 50);
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

        this.checkBoxPanel.add(this.checkBoxCreateBackup);
        this.checkBoxPanel.add(this.checkBoxDownloadOptiFine);
        this.checkBoxPanel.add(this.checkBoxDownloadJustEnoughItems);
    }

    private void createButtons() {
        this.buttonPanel = new JPanel();
        this.buttonPanel.setLayout(new BoxLayout(this.buttonPanel, BoxLayout.X_AXIS));

        this.bagConstraints.gridx = 0;
        this.bagConstraints.gridy = 1;
        this.bagConstraints.ipady = 5;
        this.bagConstraints.insets = new Insets(0, 50, 20, 50);
        this.bagConstraints.weightx = 1;

        this.mainFrame.add(this.buttonPanel, this.bagConstraints);

        this.buttonDownloadMods = createButton("Download Mods", action -> {
            onButtonDownloadModsClicked();
        });
        this.buttonDownloadForge = createButton("Download Forge", action -> {
            onButtonDownloadForgeClicked();
        });

        this.buttonPanel.add(this.buttonDownloadMods);
        this.buttonPanel.add(this.buttonDownloadForge);
    }

    public void updateCheckBox() {
        this.checkBoxCreateBackup.setSelected(true);
        this.checkBoxDownloadOptiFine.setSelected(true);
        this.checkBoxDownloadJustEnoughItems.setSelected(true);
    }

    private void toggleElements(boolean enabled) {
        this.checkBoxCreateBackup.setEnabled(enabled);
        this.checkBoxDownloadOptiFine.setEnabled(enabled);
        this.checkBoxDownloadJustEnoughItems.setEnabled(enabled);

        this.buttonDownloadForge.setEnabled(enabled);
        this.buttonDownloadMods.setEnabled(enabled);
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

        SwingUtilities.invokeLater(() -> {
            if (this.checkBoxCreateBackup.isSelected()) {
                this.modInstaller.backupModFolder();
            }
            this.modInstaller.createModFolder();
            this.modInstaller.downloadMods(() -> {
                toggleElements(true);
            });
        });


    }

    private void onButtonDownloadForgeClicked() {
        if (this.forgeInstaller == null) return;
        toggleElements(false);

        SwingUtilities.invokeLater(() -> {
            this.forgeInstaller.setup();
            this.forgeInstaller.download(() -> {
                this.forgeInstaller.install();
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

}
