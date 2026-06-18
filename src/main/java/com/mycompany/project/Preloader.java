package com.mycompany.project;

import javax.swing.*;
import java.awt.*;

public class Preloader extends JWindow {

    public Preloader() {
        setSize(300, 300);
        setLocationRelativeTo(null);

        // Simple solid background color (light gray)
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.setBackground(new Color(240, 240, 240)); // light gray

        JLabel loadingLabel = new JLabel("Loading...", JLabel.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        loadingLabel.setForeground(Color.DARK_GRAY);
        loadingLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Spinner
        ImageIcon spinner = new ImageIcon(getClass().getResource("/images/books_flip.gif"));
        JLabel spinnerLabel = new JLabel(spinner);
        spinnerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        content.add(spinnerLabel, BorderLayout.CENTER);
        content.add(loadingLabel, BorderLayout.SOUTH);
        content.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        setContentPane(content);
    }

    public void showAndRun(Runnable afterLoading) {
        SwingUtilities.invokeLater(() -> setVisible(true));

        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate loading
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            SwingUtilities.invokeLater(() -> {
                setVisible(false);
                dispose();
                afterLoading.run();
            });
        }).start();
    }
}




// package com.mycompany.project;

// import javax.swing.*;
// import java.awt.*;

// public class Preloader extends JWindow {

//     public Preloader() {
//         setSize(300, 300);
//         setLocationRelativeTo(null);
//         setOpacity(0f); // Start fully transparent for fade-in

//         // Simple solid background color (light gray)
//         JPanel content = new JPanel();
//         content.setLayout(new BorderLayout());
//         content.setBackground(new Color(240, 240, 240)); // light gray

//         JLabel loadingLabel = new JLabel("Loading...", JLabel.CENTER);
//         loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
//         loadingLabel.setForeground(Color.DARK_GRAY);
//         loadingLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

//         // Spinner
//         ImageIcon spinner = new ImageIcon(getClass().getResource("/images/books_flip.gif"));
//         JLabel spinnerLabel = new JLabel(spinner);
//         spinnerLabel.setHorizontalAlignment(SwingConstants.CENTER);

//         content.add(spinnerLabel, BorderLayout.CENTER);
//         content.add(loadingLabel, BorderLayout.SOUTH);
//         content.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

//         setContentPane(content);
//     }

//     public void showAndRun(Runnable afterLoading) {
//         SwingUtilities.invokeLater(() -> {
//             setVisible(true);
//             fadeIn(() -> new Thread(() -> {
//                 try {
//                     Thread.sleep(5000); // Simulate loading
//                 } catch (InterruptedException e) {
//                     e.printStackTrace();
//                 }
//                 SwingUtilities.invokeLater(() -> fadeOut(afterLoading));
//             }).start());
//         });
//     }

//     private void fadeIn(Runnable afterFadeIn) {
//         Timer timer = new Timer(50, null);
//         final float[] opacity = {0f};

//         timer.addActionListener(e -> {
//             opacity[0] += 0.05f;
//             if (opacity[0] >= 1f) {
//                 setOpacity(1f);
//                 timer.stop();
//                 afterFadeIn.run();
//             } else {
//                 setOpacity(opacity[0]);
//             }
//         });

//         timer.start();
//     }

//     private void fadeOut(Runnable afterFadeOut) {
//         Timer timer = new Timer(50, null);
//         final float[] opacity = {1f};

//         timer.addActionListener(e -> {
//             opacity[0] -= 0.05f;
//             if (opacity[0] <= 0f) {
//                 setOpacity(0f);
//                 timer.stop();
//                 setVisible(false);
//                 dispose();
//                 afterFadeOut.run();
//             } else {
//                 setOpacity(opacity[0]);
//             }
//         });

//         timer.start();
//     }
// }
