package com.coffeecode.ui.toolbar;

import com.coffeecode.ui.service.MainFrameService;

public class ToolbarControlManager {

    private final ToolbarPanel toolbarPanel;
    private final MainFrameService mainFrameService;

    public ToolbarControlManager(ToolbarPanel toolbarPanel, MainFrameService mainFrameService) {
        this.toolbarPanel = toolbarPanel;
        this.mainFrameService = mainFrameService;
        setupInitialState();
    }

    private void setupInitialState() {
        toolbarPanel.getPauseButton().setEnabled(false);
        toolbarPanel.getStopButton().setEnabled(false);
    }

    public void handlePlayAction() {
        toolbarPanel.getPlayButton().setEnabled(false);
        toolbarPanel.getPauseButton().setEnabled(true);
        toolbarPanel.getStopButton().setEnabled(true);
        disableEditingControls();
        mainFrameService.startAlgorithm();
    }

    public void handlePauseAction() {
        toolbarPanel.getPauseButton().setEnabled(false);
        toolbarPanel.getPlayButton().setEnabled(true);
        mainFrameService.pauseAlgorithm();
    }

    public void handleStopAction() {
        toolbarPanel.getStopButton().setEnabled(false);
        toolbarPanel.getPlayButton().setEnabled(true);
        toolbarPanel.getPauseButton().setEnabled(false);
        enableEditingControls();
        mainFrameService.stopAlgorithm();
    }

    private void disableEditingControls() {
        toolbarPanel.getAddNodeButton().setEnabled(false);
        toolbarPanel.getRemoveNodeButton().setEnabled(false);
        toolbarPanel.getAddEdgeButton().setEnabled(false);
        toolbarPanel.getRemoveEdgeButton().setEnabled(false);
        toolbarPanel.getClearAllButton().setEnabled(false);
    }

    private void enableEditingControls() {
        toolbarPanel.getAddNodeButton().setEnabled(true);
        toolbarPanel.getRemoveNodeButton().setEnabled(true);
        toolbarPanel.getAddEdgeButton().setEnabled(true);
        toolbarPanel.getRemoveEdgeButton().setEnabled(true);
        toolbarPanel.getClearAllButton().setEnabled(true);
    }
}
