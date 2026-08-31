package compilator;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Row-header ruler that paints 1-based line numbers using the editor font
 * and always uses {@code \n} metrics, so Windows and Unix editors stay aligned.
 */
public class LineNumberRuler extends JComponent {

    private final JTextArea textArea;
    private final Color background;
    private final Color foreground;
    private final Color currentLine;

    public LineNumberRuler(JTextArea textArea, Color background, Color foreground, Color currentLine) {
        this.textArea = textArea;
        this.background = background;
        this.foreground = foreground;
        this.currentLine = currentLine;
        setOpaque(true);
        setFont(textArea.getFont());
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                revalidateAndRepaint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                revalidateAndRepaint();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                revalidateAndRepaint();
            }
        });
        textArea.addCaretListener(e -> repaint());
        textArea.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                revalidateAndRepaint();
            }
        });
        textArea.addPropertyChangeListener("font", evt -> {
            setFont(textArea.getFont());
            revalidateAndRepaint();
        });
    }

    private void revalidateAndRepaint() {
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        int lines = Math.max(1, textArea.getLineCount());
        int digits = Math.max(3, String.valueOf(lines).length());
        FontMetrics metrics = getFontMetrics(getFont());
        int width = metrics.charWidth('0') * digits + 16;
        int height = Math.max(textArea.getHeight(), textArea.getPreferredSize().height);
        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(background);
        g2.fillRect(0, 0, getWidth(), getHeight());

        Font font = textArea.getFont();
        g2.setFont(font);
        FontMetrics metrics = g2.getFontMetrics();
        int lineHeight = textArea.getFontMetrics(font).getHeight();
        int ascent = textArea.getFontMetrics(font).getAscent();
        int lines = Math.max(1, textArea.getLineCount());
        int caretLine = 0;
        try {
            caretLine = textArea.getLineOfOffset(textArea.getCaretPosition());
        } catch (Exception ignored) {
            caretLine = 0;
        }

        int y = 0;
        try {
            y = textArea.getInsets().top;
        } catch (Exception ignored) {
            y = 0;
        }

        for (int i = 0; i < lines; i++) {
            String number = String.valueOf(i + 1);
            int x = getWidth() - metrics.stringWidth(number) - 8;
            int baseline = y + ascent;
            if (i == caretLine) {
                g2.setColor(currentLine);
                g2.fillRect(0, y, getWidth(), lineHeight);
                g2.setColor(foreground);
            } else {
                g2.setColor(foreground);
            }
            g2.drawString(number, x, baseline);
            y += lineHeight;
        }
        g2.dispose();
    }
}
