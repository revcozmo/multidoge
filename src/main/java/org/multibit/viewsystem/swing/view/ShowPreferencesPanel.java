package org.multibit.viewsystem.swing.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.multibit.controller.MultiBitController;
import org.multibit.model.Data;
import org.multibit.model.DataProvider;
import org.multibit.model.Item;
import org.multibit.model.MultiBitModel;
import org.multibit.viewsystem.View;
import org.multibit.viewsystem.swing.MultiBitFrame;
import org.multibit.viewsystem.swing.action.ChooseFontAction;
import org.multibit.viewsystem.swing.action.ShowPreferencesSubmitAction;
import org.multibit.viewsystem.swing.view.components.FontSizer;
import org.multibit.viewsystem.swing.view.components.MultiBitButton;
import org.multibit.viewsystem.swing.view.components.MultiBitLabel;
import org.multibit.viewsystem.swing.view.components.MultiBitTextField;

/**
 * The show preferences view
 */
public class ShowPreferencesPanel extends JPanel implements View, DataProvider {

    private static final long serialVersionUID = 191352298245057705L;

    private static final int FEE_TEXT_FIELD_WIDTH = 100;
    private static final int FEE_TEXT_FIELD_HEIGHT = 30;
    
    private MultiBitController controller;

    SortedSet<LanguageData> languageDataSet;

    private JRadioButton useDefaultLocale;
    private JComboBox languageComboBox;

    private MultiBitTextField feeTextField;
    private String originalFee;

    private JRadioButton ignoreAll;
    private JRadioButton fillAutomatically;
    private JRadioButton askEveryTime;
    
    private MultiBitLabel fontNameTextLabel;
    private MultiBitLabel fontStyleTextLabel;
    private MultiBitLabel fontSizeTextLabel;
    
    private String originalFontName;
    private String originalFontStyle;
    private String originalFontSize;
    
    private Data data;   
  
    private Font selectedFont;
    private FontSizer fontSizer;
    

    /**
     * Creates a new {@link ShowPreferencesPanel}.
     */
    public ShowPreferencesPanel(MultiBitController controller, MultiBitFrame mainFrame) {
        this.controller = controller;
   
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 0, 1, 0),
                BorderFactory.createMatteBorder(1, 0, 1, 0, MultiBitFrame.DARK_BACKGROUND_COLOR.darker())));
        setBackground(MultiBitFrame.BACKGROUND_COLOR);

        this.controller = controller;
        fontSizer = new FontSizer(controller);

        data = new Data();

        initUI();
        applyComponentOrientation(ComponentOrientation.getOrientation(controller.getLocaliser().getLocale()));
    }

    /**
     * show preferences panel
     */
    public void displayView() {
        String sendFeeString = controller.getModel().getUserPreference(MultiBitModel.SEND_FEE);

        if (sendFeeString == null || sendFeeString == "") {
            sendFeeString = controller.getLocaliser().bitcoinValueToString4(MultiBitModel.SEND_MINIMUM_FEE, false, false);
        }
        originalFee = sendFeeString;
        feeTextField.setText(sendFeeString);

        String showDialogString = controller.getModel().getUserPreference(MultiBitModel.OPEN_URI_SHOW_DIALOG);
        String useUriString = controller.getModel().getUserPreference(MultiBitModel.OPEN_URI_USE_URI);
        
        if (!(Boolean.FALSE.toString().equalsIgnoreCase(showDialogString))) {
            // missing showDialog or it is set to true
            askEveryTime.setSelected(true);
        } else {
            if (!(Boolean.FALSE.toString().equalsIgnoreCase(useUriString))) {
                // missing useUri or it is set to true
                fillAutomatically.setSelected(true);
            } else {
                // useUri set to false
                ignoreAll.setSelected(true);
            }
        }
        
        String fontNameString = controller.getModel().getUserPreference(MultiBitModel.FONT_NAME);
        if (fontNameString == null || "".equals(fontNameString)) {
            fontNameString = MultiBitFrame.MULTIBIT_DEFAULT_FONT_NAME;
        }
        originalFontName = fontNameString;
       
        int fontStyle = MultiBitFrame.MULTIBIT_DEFAULT_FONT_STYLE;
        String fontStyleString = controller.getModel().getUserPreference(MultiBitModel.FONT_STYLE);
        if (fontStyleString != null && !"".equals(fontStyleString)) {
            try {
                fontStyle = Integer.parseInt(fontStyleString);
            } catch (NumberFormatException nfe) {
                // use default
            }
        }      
        originalFontStyle = "" + fontStyle;

        int fontSize = MultiBitFrame.MULTIBIT_DEFAULT_FONT_SIZE;
        String fontSizeString = controller.getModel().getUserPreference(MultiBitModel.FONT_SIZE);
        if (fontSizeString != null && !"".equals(fontSizeString)) {
            try {
                fontSize = Integer.parseInt(fontSizeString);
            } catch (NumberFormatException nfe) {
                // use default
            }
        }               
        originalFontSize = "" + fontSize;
       
        setSelectedFont(new Font(fontNameString, fontStyle, fontSize));
    }

    public void displayMessage(String messageKey, Object[] messageData, String titleKey) {
        // not implemented on this view
    }

    public void navigateAwayFromView(int nextViewId, int relationshipOfNewViewToPrevious) {
    }

    private void initUI() {
        setMinimumSize(new Dimension(550, 160));

        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(new GridBagLayout());

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 0.06;
        constraints.anchor = GridBagConstraints.CENTER;
        JPanel fillerPanel1 = new JPanel();
        fillerPanel1.setOpaque(false);
        add(fillerPanel1, constraints);

        MultiBitLabel titleLabel = new MultiBitLabel("", controller);
        titleLabel.setHorizontalTextPosition(JLabel.LEADING);
        titleLabel.setText(controller.getLocaliser().getString("showPreferencesPanel.title"));

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 1.8;
        constraints.weighty = 0.06;
        constraints.anchor = GridBagConstraints.CENTER;
        add(titleLabel, constraints);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.weighty = 1.6;
        constraints.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;
        add(createFontChooserPanel(), constraints);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.weighty = 1.6;
        constraints.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;
        add(createLanguagePanel(), constraints);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.weighty = 1.6;
        constraints.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;
        add(createBrowserIntegrationPanel(), constraints);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;
        add(createFeePanel(), constraints);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.gridwidth = 1;
        constraints.weightx = 0.4;
        constraints.weighty = 0.06;
        constraints.anchor = GridBagConstraints.LINE_START;
        add(createButtonPanel(), constraints);

        JLabel filler1 = new JLabel();
        filler1.setOpaque(false);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 7;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.weighty = 20;
        constraints.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;
        add(filler1, constraints);
    }

    private JPanel createLanguagePanel() {
        // language radios
        JPanel languagePanel = new JPanel(new GridBagLayout());
        TitledBorder titledBorder = BorderFactory.createTitledBorder(controller.getLocaliser().getString("showPreferencesPanel.languageTitle"));
        setAdjustedFont(titledBorder);
        Border border = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0), titledBorder);
                
        languagePanel.setBorder(border);
        languagePanel.setOpaque(false);

        GridBagConstraints constraints = new GridBagConstraints();

        ButtonGroup languageUsageGroup = new ButtonGroup();
        useDefaultLocale = new JRadioButton(controller.getLocaliser().getString("showPreferencesPanel.useDefault"));
        useDefaultLocale.setOpaque(false);
        
        setAdjustedFont(useDefaultLocale);
        
        JRadioButton useSpecific = new JRadioButton(controller.getLocaliser().getString("showPreferencesPanel.useSpecific"));
        useSpecific.setOpaque(false);
        setAdjustedFont(useSpecific);
        
        ItemListener itemListener = new ChangeLanguageUsageItemListener();
        useDefaultLocale.addItemListener(itemListener);
        useSpecific.addItemListener(itemListener);
        languageUsageGroup.add(useDefaultLocale);
        languageUsageGroup.add(useSpecific);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.2;
        constraints.weighty = 0.3;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        languagePanel.add(useDefaultLocale, constraints);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0.2;
        constraints.weighty = 0.3;
        constraints.anchor = GridBagConstraints.LINE_START;
        languagePanel.add(useSpecific, constraints);

        // language combo box
        int numberOfLanguages = Integer.parseInt(controller.getLocaliser().getString("showPreferencesPanel.numberOfLanguages"));

        // languages are added to the combo box in alphabetic order
        languageDataSet = new TreeSet<LanguageData>();

        for (int i = 0; i < numberOfLanguages; i++) {
            String languageCode = controller.getLocaliser().getString("showPreferencesPanel.languageCode." + (i + 1));
            String language = controller.getLocaliser().getString("showPreferencesPanel.language." + (i + 1));

            LanguageData languageData = new LanguageData();
            languageData.languageCode = languageCode;
            languageData.language = language;
            languageData.image = createImageIcon(languageCode);
            languageData.image.setDescription(language);
            languageDataSet.add(languageData);
        }

        Integer[] indexArray = new Integer[languageDataSet.size()];
        int index = 0;
        for (@SuppressWarnings("unused") LanguageData languageData : languageDataSet) {
            indexArray[index] = index;
            index++;
        }
        languageComboBox = new JComboBox(indexArray);
        ComboBoxRenderer renderer = new ComboBoxRenderer();
        renderer.setPreferredSize(new Dimension(150, 30));
        languageComboBox.setRenderer(renderer);

        // get the languageCode value stored in the model
        String userLanguageCode = controller.getModel().getUserPreference(MultiBitModel.USER_LANGUAGE_CODE);
        if (userLanguageCode == null || MultiBitModel.USER_LANGUAGE_IS_DEFAULT.equals(userLanguageCode)) {
            useDefaultLocale.setSelected(true);
            languageComboBox.setEnabled(false);
        } else {
            useSpecific.setSelected(true);
            int startingIndex = 0;
            Integer languageCodeIndex = 0;
            for (LanguageData languageData : languageDataSet) {
                if (languageData.languageCode.equals(userLanguageCode)) {
                    languageCodeIndex = startingIndex;
                    break;
                }
                startingIndex++;
            }
            if (languageCodeIndex != 0) {
                languageComboBox.setSelectedItem(languageCodeIndex.intValue());
                languageComboBox.setEnabled(true);
            }
        }

        // store original value for use by submit action
        Item languageItem = new Item(MultiBitModel.USER_LANGUAGE_CODE);
        languageItem.setOriginalValue(userLanguageCode);
        data.addItem(MultiBitModel.USER_LANGUAGE_CODE, languageItem);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.weightx = 0.8;
        constraints.weighty = 0.6;
        constraints.anchor = GridBagConstraints.LINE_START;
        languagePanel.add(languageComboBox, constraints);

        // main panel layout
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 0.15;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_END;

        return languagePanel;
    }

    private JPanel createFeePanel() {
        JPanel feePanel = new JPanel(new GridBagLayout());
        TitledBorder titledBorder = BorderFactory.createTitledBorder(controller.getLocaliser().getString("showPreferencesPanel.feeTitle"));
        setAdjustedFont(titledBorder);
        Border border = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0), titledBorder);
        feePanel.setBorder(border);
        
        feePanel.setOpaque(false);

        GridBagConstraints constraints = new GridBagConstraints();

        MultiBitLabel feeLabel = new MultiBitLabel(controller.getLocaliser().getString("showPreferencesPanel.feeLabel.text"), controller);
        feeLabel.setToolTipText(controller.getLocaliser().getString("showPreferencesPanel.feeLabel.tooltip"));
        MultiBitLabel feeCurrencyLabel = new MultiBitLabel("BTC", controller);

        String sendFeeString = controller.getModel().getUserPreference(MultiBitModel.SEND_FEE);

        if (sendFeeString == null || sendFeeString == "") {
            sendFeeString = controller.getLocaliser().bitcoinValueToString4(MultiBitModel.SEND_MINIMUM_FEE, false, false);
        }
        originalFee = sendFeeString;

        feeTextField = new MultiBitTextField("", 10, controller);
        feeTextField.setHorizontalAlignment(JLabel.TRAILING);
        feeTextField.setMinimumSize(new Dimension(FEE_TEXT_FIELD_WIDTH, FEE_TEXT_FIELD_HEIGHT));
        feeTextField.setPreferredSize(new Dimension(FEE_TEXT_FIELD_WIDTH, FEE_TEXT_FIELD_HEIGHT));
        feeTextField.setMaximumSize(new Dimension(FEE_TEXT_FIELD_WIDTH, FEE_TEXT_FIELD_HEIGHT));

        feeTextField.setText(sendFeeString);
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.3;
        constraints.weighty = 0.3;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        feePanel.add(feeLabel, constraints);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 0.3;
        constraints.weighty = 0.3;
        constraints.anchor = GridBagConstraints.LINE_START;
        feePanel.add(feeTextField, constraints);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.weightx = 0.2;
        constraints.weighty = 0.3;
        constraints.anchor = GridBagConstraints.LINE_START;
        feePanel.add(feeCurrencyLabel, constraints);

        return feePanel;
    }

    private JPanel createFontChooserPanel() {
        JPanel fontChooserPanel = new JPanel(new GridBagLayout());
        TitledBorder titledBorder = BorderFactory.createTitledBorder(controller.getLocaliser().getString("showPreferencesPanel.fontChooserTitle"));
        setAdjustedFont(titledBorder);
        Border border = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0), titledBorder);
        fontChooserPanel.setBorder(border);
        
        fontChooserPanel.setOpaque(false);

        GridBagConstraints constraints = new GridBagConstraints();

        MultiBitLabel fontNameLabel = new MultiBitLabel(controller.getLocaliser().getString("fontChooser.fontName"), controller);
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.3;
        constraints.weighty = 0.5;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        fontChooserPanel.add(fontNameLabel, constraints);
        
        fontNameTextLabel = new MultiBitLabel("", controller);
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 0.3;
        constraints.weighty = 0.5;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        fontChooserPanel.add(fontNameTextLabel, constraints);

        MultiBitLabel fontStyleLabel = new MultiBitLabel(controller.getLocaliser().getString("fontChooser.fontStyle"), controller);
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0.3;
        constraints.weighty = 0.5;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        fontChooserPanel.add(fontStyleLabel, constraints);        

        fontStyleTextLabel = new MultiBitLabel("", controller);
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 0.3;
        constraints.weighty = 0.5;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        fontChooserPanel.add(fontStyleTextLabel, constraints);
        
        MultiBitLabel fontSizeLabel = new MultiBitLabel(controller.getLocaliser().getString("fontChooser.fontSize"), controller);
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 0.3;
        constraints.weighty = 0.5;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        fontChooserPanel.add(fontSizeLabel, constraints);
        
        fontSizeTextLabel = new MultiBitLabel("", controller);
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.weightx = 0.3;
        constraints.weighty = 0.5;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        fontChooserPanel.add(fontSizeTextLabel, constraints);
        
        ChooseFontAction chooseFontAction = new ChooseFontAction(controller, this, null);
        MultiBitButton fontChooserButton = new MultiBitButton(chooseFontAction, controller);
        
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.weightx = 0.3;
        constraints.weighty = 0.5;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        fontChooserPanel.add(fontChooserButton, constraints);

        return fontChooserPanel;
    }
    private JPanel createBrowserIntegrationPanel() {
        JPanel browserIntegrationPanel = new JPanel(new GridBagLayout());
        TitledBorder titledBorder = BorderFactory.createTitledBorder(controller.getLocaliser().getString("showPreferencesPanel.browserIntegrationTitle"));
        setAdjustedFont(titledBorder);
        Border border = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0), titledBorder);
        browserIntegrationPanel.setBorder(border);

        browserIntegrationPanel.setOpaque(false);

        GridBagConstraints constraints = new GridBagConstraints();

        MultiBitLabel messageLabel = new MultiBitLabel(controller.getLocaliser().getString("showPreferencesPanel.browserIntegration.messageText"), controller);
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.2;
        constraints.weighty = 0.3;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        browserIntegrationPanel.add(messageLabel, constraints);

        ButtonGroup browserIntegrationGroup = new ButtonGroup();
        ignoreAll = new JRadioButton(controller.getLocaliser().getString("showPreferencesPanel.ignoreAll"));
        ignoreAll.setOpaque(false);
        setAdjustedFont(ignoreAll);
        
        fillAutomatically = new JRadioButton(controller.getLocaliser().getString("showPreferencesPanel.fillAutomatically"));
        fillAutomatically.setOpaque(false);
        setAdjustedFont(fillAutomatically);
        
        askEveryTime = new JRadioButton(controller.getLocaliser().getString("showPreferencesPanel.askEveryTime"));
        askEveryTime.setOpaque(false);
        setAdjustedFont(askEveryTime);
        
        browserIntegrationGroup.add(ignoreAll);
        browserIntegrationGroup.add(fillAutomatically);
        browserIntegrationGroup.add(askEveryTime);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0.2;
        constraints.weighty = 0.3;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        browserIntegrationPanel.add(ignoreAll, constraints);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 0.2;
        constraints.weighty = 0.3;
        constraints.anchor = GridBagConstraints.LINE_START;
        browserIntegrationPanel.add(fillAutomatically, constraints);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.weightx = 0.2;
        constraints.weighty = 0.3;
        constraints.anchor = GridBagConstraints.LINE_START;
        browserIntegrationPanel.add(askEveryTime, constraints);

        return browserIntegrationPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.TRAILING);
        buttonPanel.setLayout(flowLayout);

        ShowPreferencesSubmitAction submitAction = new ShowPreferencesSubmitAction(controller, this);
        MultiBitButton submitButton = new MultiBitButton(submitAction, controller);
        buttonPanel.add(submitButton);

        return buttonPanel;
    }

    class ChangeLanguageUsageItemListener implements ItemListener {
        public ChangeLanguageUsageItemListener() {

        }

        public void itemStateChanged(ItemEvent e) {
            if (e.getSource().equals(useDefaultLocale)) {
                languageComboBox.setEnabled(false);
            } else {
                languageComboBox.setEnabled(true);
            }
        }
    }

    private ImageIcon createImageIcon(String text) {
        Font font = new Font("Dialog", Font.PLAIN, 16);

        BufferedImage bimg = new BufferedImage(26, 20, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bimg.createGraphics();

        g2.setColor(Color.WHITE);
        g2.setFont(font);
        g2.drawString(text, 3, 16);

        return new ImageIcon(bimg);
    }

    public Data getData() {
        Item languageItem = data.getItem(MultiBitModel.USER_LANGUAGE_CODE);
        if (useDefaultLocale.isSelected()) {
            languageItem.setNewValue(MultiBitModel.USER_LANGUAGE_IS_DEFAULT);
        } else {
            Integer selectedLanguageIndex = (Integer) languageComboBox.getSelectedItem();
            if (selectedLanguageIndex != null) {
                int loopIndex = 0;
                for (LanguageData languageData : languageDataSet) {
                    if (selectedLanguageIndex.intValue() == loopIndex) {
                        String newLanguageCode = languageData.languageCode;
                        languageItem.setNewValue(newLanguageCode);
                        break;
                    }
                    loopIndex++;
                }
            }
        }

        Item feeItem = new Item(MultiBitModel.SEND_FEE);
        feeItem.setOriginalValue(originalFee);
        feeItem.setNewValue(feeTextField.getText());
        data.addItem(MultiBitModel.SEND_FEE, feeItem);
        
        Item showDialogItem = new Item(MultiBitModel.OPEN_URI_SHOW_DIALOG);
        showDialogItem.setNewValue((new Boolean((askEveryTime.isSelected()))).toString());
        data.addItem(MultiBitModel.OPEN_URI_SHOW_DIALOG, showDialogItem );

        Item useUriItem = new Item(MultiBitModel.OPEN_URI_USE_URI);
        boolean useUri = true;
        if (ignoreAll.isSelected()) {
            useUri = false;
        }
        useUriItem.setNewValue((new Boolean(useUri)).toString());
        data.addItem(MultiBitModel.OPEN_URI_USE_URI, useUriItem );

        Item fontNameItem = new Item(MultiBitModel.FONT_NAME);
        fontNameItem.setOriginalValue(originalFontName);
        fontNameItem.setNewValue(selectedFont.getFamily());
        data.addItem(MultiBitModel.FONT_NAME, fontNameItem );
        
        Item fontStyleItem = new Item(MultiBitModel.FONT_STYLE);
        fontStyleItem.setOriginalValue(originalFontStyle);
        fontStyleItem.setNewValue("" + selectedFont.getStyle());
        data.addItem(MultiBitModel.FONT_STYLE, fontStyleItem );
        
        Item fontSizeItem = new Item(MultiBitModel.FONT_SIZE);
        fontSizeItem.setOriginalValue(originalFontSize);
        fontSizeItem.setNewValue("" + selectedFont.getSize());
        data.addItem(MultiBitModel.FONT_SIZE, fontSizeItem );
        
        return data;
    }

    class ComboBoxRenderer extends MultiBitLabel implements ListCellRenderer {
        private static final long serialVersionUID = -3301957214353702172L;

        public ComboBoxRenderer() {
            super("", controller);
            setOpaque(true);
            setHorizontalAlignment(LEADING);
            setVerticalAlignment(CENTER);
            
            setComponentOrientation(ComponentOrientation.getOrientation(controller.getLocaliser().getLocale()));
        }

        /*
         * This method finds the image and text corresponding to the selected
         * value and returns the label, set up to display the text and image.
         */
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            // Get the selected index. (The index param isn't
            // always valid, so just use the value.)
            int selectedIndex = 0;
            if (value != null) {
                selectedIndex = (Integer) value;
            }
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            // Set the icon and text. If icon was null, say so.
            int loopIndex = 0;
            for (LanguageData languageData : languageDataSet) {
                if (selectedIndex == loopIndex) {
                    ImageIcon icon = languageData.image;
                    String language =languageData.language;
                    setIcon(icon);
                    setText(language);
                    break;
                }
                loopIndex++;
            }

            setFont(list.getFont());

            return this;
        }
    }

    class LanguageData implements Comparable<LanguageData> {
        public String languageCode;
        public String language;
        public ImageIcon image;

        @Override
        public int compareTo(LanguageData other) {
            return languageCode.compareTo(other.languageCode);
        }
    }

    @Override
    public void updateView() {
        // TODO Auto-generated method stub
        
    }

    private void setAdjustedFont(Component component) {
        String fontSizeString = controller.getModel().getUserPreference(MultiBitModel.FONT_SIZE);
        FontSizer fontSizer = new FontSizer(controller);
        if (fontSizeString == null || "".equals(fontSizeString)) {
            fontSizer.setAdjustedFont(component, MultiBitFrame.MULTIBIT_DEFAULT_FONT_SIZE);
        } else {
            try {
                fontSizer.setAdjustedFont(component, Integer.parseInt(fontSizeString));
            } catch (NumberFormatException nfe) {
                fontSizer.setAdjustedFont(component, MultiBitFrame.MULTIBIT_DEFAULT_FONT_SIZE);
            }
        }
    }
      
    protected void setAdjustedFont(TitledBorder border) {
        String fontSizeString = controller.getModel().getUserPreference(MultiBitModel.FONT_SIZE);
        FontSizer fontSizer = new FontSizer(controller);
        if (fontSizeString == null || "".equals(fontSizeString)) {
            fontSizer.setAdjustedFont(border, MultiBitFrame.MULTIBIT_DEFAULT_FONT_SIZE);
        } else {
            try {
                fontSizer.setAdjustedFont(border, Integer.parseInt(fontSizeString));
            } catch (NumberFormatException nfe) {
                fontSizer.setAdjustedFont(border,  MultiBitFrame.MULTIBIT_DEFAULT_FONT_SIZE);
            }
        }
    }
    
    public void setSelectedFont(Font selectedFont) {
        this.selectedFont = selectedFont;
        
        fontNameTextLabel.setText(selectedFont.getFamily());
        fontStyleTextLabel.setText("" + selectedFont.getStyle());              
        fontSizeTextLabel.setText("" + selectedFont.getSize());
        
        invalidate();
        validate();
        repaint();
    }

    public Font getSelectedFont() {
        return selectedFont;
    }
}