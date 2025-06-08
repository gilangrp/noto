package com.noto.utility;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Utility class for animations and visual effects
 */
public class AnimationUtils {
    
    /**
     * Creates a fade-in animation effect for a JFrame
     * @param frame The JFrame to animate
     * @param duration The duration of the animation in milliseconds
     */
    public static void fadeIn(JFrame frame, int duration) {
        final float[] opacity = {0f};
        final long startTime = System.currentTimeMillis();
        
        // Set frame to be transparent initially
        frame.setOpacity(0f);
        
        // Create timer for animation
        Timer timer = new Timer(15, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            opacity[0] = Math.min(1f, (float) elapsed / duration);
            
            frame.setOpacity(opacity[0]);
            
            if (opacity[0] >= 1f) {
                ((Timer) e.getSource()).stop();
            }
        });
        
        timer.start();
    }
    
    /**
     * Creates a fade-out animation effect for a JFrame
     * @param frame The JFrame to animate
     * @param duration The duration of the animation in milliseconds
     * @param onComplete Runnable to execute after animation completes
     */
    public static void fadeOut(JFrame frame, int duration, Runnable onComplete) {
        final float[] opacity = {1f};
        final long startTime = System.currentTimeMillis();
        
        // Create timer for animation
        Timer timer = new Timer(15, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            opacity[0] = Math.max(0f, 1f - (float) elapsed / duration);
            
            frame.setOpacity(opacity[0]);
            
            if (opacity[0] <= 0f) {
                ((Timer) e.getSource()).stop();
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
        
        timer.start();
    }
    
    /**
     * Animates a button hover effect
     * @param button The button to animate
     * @param isHovering Whether the mouse is hovering over the button
     */
    public static void animateButtonHover(JButton button, boolean isHovering) {
        int startWidth = isHovering ? 200 : 205;
        int endWidth = isHovering ? 205 : 200;
        int startHeight = isHovering ? 40 : 42;
        int endHeight = isHovering ? 42 : 40;
        
        Color startColor = isHovering ? 
            new Color(65, 105, 225) : // Royal Blue
            new Color(45, 85, 205);   // Darker blue
        
        Color endColor = isHovering ? 
            new Color(45, 85, 205) :  // Darker blue
            new Color(65, 105, 225);  // Royal Blue
        
        animateButton(button, 
                     new Dimension(startWidth, startHeight), 
                     new Dimension(endWidth, endHeight),
                     startColor,
                     endColor,
                     150);
    }
    
    /**
     * Animates a button's size and color
     */
    private static void animateButton(JButton button, 
                                     Dimension fromSize, 
                                     Dimension toSize,
                                     Color fromColor,
                                     Color toColor,
                                     int duration) {
        final long startTime = System.currentTimeMillis();
        
        Timer timer = new Timer(10, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1f, (float) elapsed / duration);
            
            // Interpolate size
            int width = (int) (fromSize.width + (toSize.width - fromSize.width) * progress);
            int height = (int) (fromSize.height + (toSize.height - fromSize.height) * progress);
            
            // Interpolate color
            int r = (int) (fromColor.getRed() + (toColor.getRed() - fromColor.getRed()) * progress);
            int g = (int) (fromColor.getGreen() + (toColor.getGreen() - fromColor.getGreen()) * progress);
            int b = (int) (fromColor.getBlue() + (toColor.getBlue() - fromColor.getBlue()) * progress);
            
            button.setPreferredSize(new Dimension(width, height));
            button.setMaximumSize(new Dimension(width, height));
            button.setBackground(new Color(r, g, b));
            button.revalidate();
            
            if (progress >= 1f) {
                ((Timer) e.getSource()).stop();
            }
        });
        
        timer.start();
    }
    
    /**
     * Creates a shake animation for input fields when validation fails
     * @param component The component to animate
     */
    public static void shakeComponent(JComponent component) {
        final int originalX = component.getLocation().x;
        final int[] offsets = {-5, 5, -5, 5, -3, 3, -2, 2, -1, 1, 0};
        final int animationSpeed = 30;
        
        Timer timer = new Timer(animationSpeed, null);
        timer.addActionListener(new ActionListener() {
            int position = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (position < offsets.length) {
                    component.setLocation(originalX + offsets[position], component.getLocation().y);
                    position++;
                } else {
                    component.setLocation(originalX, component.getLocation().y);
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        
        timer.start();
    }
    
    /**
     * Highlights a field with a color animation (for validation feedback)
     * @param field The field to highlight
     * @param isValid Whether the input is valid
     */
    public static void highlightField(JComponent field, boolean isValid) {
        Color startColor = field.getBackground();
        Color endColor = isValid ? 
            new Color(220, 255, 220) : // Light green for valid
            new Color(255, 220, 220);  // Light red for invalid
        
        final long startTime = System.currentTimeMillis();
        final int duration = 300;
        
        Timer timer = new Timer(10, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1f, (float) elapsed / duration);
            
            // Interpolate color
            int r = (int) (startColor.getRed() + (endColor.getRed() - startColor.getRed()) * progress);
            int g = (int) (startColor.getGreen() + (endColor.getGreen() - startColor.getGreen()) * progress);
            int b = (int) (startColor.getBlue() + (endColor.getBlue() - startColor.getBlue()) * progress);
            
            field.setBackground(new Color(r, g, b));
            
            if (progress >= 1f) {
                ((Timer) e.getSource()).stop();
                
                // Fade back to original color after a delay
                if (isValid) {
                    Timer fadeBackTimer = new Timer(1000, evt -> {
                        fadeBackToOriginal(field, endColor, startColor, 500);
                        ((Timer) evt.getSource()).stop();
                    });
                    fadeBackTimer.setRepeats(false);
                    fadeBackTimer.start();
                }
            }
        });
        
        timer.start();
    }
    
    /**
     * Fades a component's background color back to original
     */
    private static void fadeBackToOriginal(JComponent component, Color fromColor, Color toColor, int duration) {
        final long startTime = System.currentTimeMillis();
        
        Timer timer = new Timer(10, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1f, (float) elapsed / duration);
            
            // Interpolate color
            int r = (int) (fromColor.getRed() + (toColor.getRed() - fromColor.getRed()) * progress);
            int g = (int) (fromColor.getGreen() + (toColor.getGreen() - fromColor.getGreen()) * progress);
            int b = (int) (fromColor.getBlue() + (toColor.getBlue() - fromColor.getBlue()) * progress);
            
            component.setBackground(new Color(r, g, b));
            
            if (progress >= 1f) {
                ((Timer) e.getSource()).stop();
            }
        });
        
        timer.start();
    }
}
