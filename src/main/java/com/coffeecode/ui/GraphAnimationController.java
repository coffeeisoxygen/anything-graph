// package com.coffeecode.ui;

// import lombok.Getter;
// import lombok.Setter;
// import javax.swing.Timer;

// @Getter
// @Setter
// public class GraphAnimationController {

//     private int delay = 500; // ms
//     private boolean isPaused = false;
//     private Timer timer;
//     private final GraphPanel graphPanel;

//     public GraphAnimationController(GraphPanel panel) {
//         this.graphPanel = panel;
//         this.timer = new Timer(delay, e -> {
//             if (!isPaused) {
//                 // Animation step will be called here
//             }
//         });
//     }

//     public void setSpeed(int speed) {
//         this.delay = 1000 - (speed * 10); // 0-100 to 1000-0ms
//         timer.setDelay(delay);
//     }

//     public void pause() {
//         isPaused = true;
//     }

//     public void resume() {
//         isPaused = false;
//     }

//     public void stop() {
//         timer.stop();
//         graphPanel.resetStates();
//     }
// }
