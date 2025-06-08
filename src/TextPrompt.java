import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * The TextPrompt class will display a prompt over top of a text component when
 * the Document of the text field is empty. The Show property is used to
 * determine the visibility of the prompt.
 */
public class TextPrompt extends JLabel implements FocusListener, DocumentListener {
    private JTextComponent component;
    private Document document;
    private boolean showPromptOnce;
    private int focusLost;

    public TextPrompt(String text, JTextComponent component) {
        this(text, component, Show.ALWAYS);
    }

    public TextPrompt(String text, JTextComponent component, Show show) {
        this.component = component;
        setShow(show);
        document = component.getDocument();

        setText(text);
        setFont(component.getFont());
        setForeground(Color.GRAY);
        setBorder(new EmptyBorder(component.getInsets()));
        setHorizontalAlignment(JLabel.LEADING);

        component.addFocusListener(this);
        document.addDocumentListener(this);

        component.setLayout(new BorderLayout());
        component.add(this);
        checkForPrompt();
    }

    /**
     * Convenience method to change the alpha value of the current foreground
     * Color to the specified value.
     *
     * @param alpha value in the range 0 - 1.0.
     */
    public void changeAlpha(float alpha) {
        changeAlpha((int) (alpha * 255));
    }

    /**
     * Convenience method to change the alpha value of the current foreground
     * Color to the specified value.
     *
     * @param alpha value in the range 0 - 255.
     */
    public void changeAlpha(int alpha) {
        alpha = alpha > 255 ? 255 : alpha < 0 ? 0 : alpha;

        Color foreground = getForeground();
        int red = foreground.getRed();
        int green = foreground.getGreen();
        int blue = foreground.getBlue();

        Color withAlpha = new Color(red, green, blue, alpha);
        super.setForeground(withAlpha);
    }

    /**
     * Convenience method to change the style of the current Font. The style
     * values are found in the Font class. Common values might be: Font.BOLD,
     * Font.ITALIC and Font.BOLD + Font.ITALIC.
     *
     * @param style value representing the the new style of the Font.
     */
    public void changeStyle(int style) {
        setFont(getFont().deriveFont(style));
    }

    /**
     * Get the Show property
     *
     * @return the Show property.
     */
    public Show getShow() {
        return Show.values()[focusLost];
    }

    /**
     * Set the prompt Show property to control when the prompt is shown. Valid
     * values are:
     *
     * Show.ALWAYS (default) - always show the prompt Show.FOCUS_GAINED - show
     * the prompt when the component gains focus (and hide the prompt when focus
     * is lost) Show.FOCUS_LOST - show the prompt when the component loses focus
     * (and hide the prompt when focus is gained)
     *
     * @param show a valid Show enum
     */
    public void setShow(Show show) {
        focusLost = show.ordinal();
    }

    /**
     * Get the showPromptOnce property
     *
     * @return the showPromptOnce property.
     */
    public boolean getShowPromptOnce() {
        return showPromptOnce;
    }

    /**
     * Show the prompt once. Once the component has gained/lost focus once, the
     * prompt will not be shown again.
     *
     * @param showPromptOnce when true the prompt will only be shown once,
     *                        otherwise it will be shown repeatedly.
     */
    public void setShowPromptOnce(boolean showPromptOnce) {
        this.showPromptOnce = showPromptOnce;
    }

    /**
     * Check whether the prompt should be visible or not. The visibility will
     * change on updates to the Document and on focus changes.
     */
    private void checkForPrompt() {
        // Text has been entered, remove the prompt
        if (document.getLength() > 0) {
            setVisible(false);
            return;
        }

        // Prompt has already been shown once, remove it
        if (showPromptOnce && getShow() == Show.FOCUS_GAINED)
            setVisible(false);
        else
            setVisible(true);
    }

    // Implement FocusListener
    public void focusGained(FocusEvent e) {
        if (getShow() == Show.FOCUS_GAINED)
            setVisible(false);
        else
            setVisible(true);
    }

    public void focusLost(FocusEvent e) {
        if (getShow() == Show.FOCUS_LOST)
            setVisible(false);
        else
            setVisible(true);
    }

    // Implement DocumentListener
    public void insertUpdate(DocumentEvent e) {
        checkForPrompt();
    }

    public void removeUpdate(DocumentEvent e) {
        checkForPrompt();
    }

    public void changedUpdate(DocumentEvent e) {
    }

    public enum Show {
        ALWAYS, FOCUS_GAINED, FOCUS_LOST;
    }
}
