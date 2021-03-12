package com.example

import com.change_vision.jude.api.inf.project.ProjectEvent
import com.change_vision.jude.api.inf.project.ProjectEventListener
import com.change_vision.jude.api.inf.ui.IPluginExtraTabView
import com.change_vision.jude.api.inf.ui.ISelectionListener
import java.awt.Component
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JPanel

const val IMAGE_KEY = "associated image"

class ImageNoteView : JPanel(), IPluginExtraTabView, ProjectEventListener, ActionListener {
    val buttonAssociate = ButtonAssociate()
    val buttonRefreashBasedOnNote = ButtonRefresh(RefreshBaseType.NOTE)
    val buttonRefreashBasedOnImage = ButtonRefresh(RefreshBaseType.IMAGE)

    init {
        val panelButtons = JPanel()
        panelButtons.layout = GridLayout(1,2)
        panelButtons.add(buttonAssociate)
        panelButtons.add(buttonRefreashBasedOnNote)
        panelButtons.add(buttonRefreashBasedOnImage)
        buttonAssociate.addActionListener(this)
        buttonRefreashBasedOnNote.addActionListener(this)
        buttonRefreashBasedOnImage.addActionListener(this)
        add(panelButtons)
    }

    override fun actionPerformed(e: ActionEvent) {
        when (e.source) {
            buttonAssociate -> {
                buttonAssociate.push(AstahAccessor.getCurrentDiagram())
            }
            buttonRefreashBasedOnNote -> {
                buttonRefreashBasedOnNote.push(AstahAccessor.getCurrentDiagram())
            }
            buttonRefreashBasedOnImage -> {
                buttonRefreashBasedOnImage.push(AstahAccessor.getCurrentDiagram())
            }
        }
    }
    override fun getTitle(): String = "Image Note"
    override fun getDescription(): String = "Image Note View"
    override fun getComponent(): Component = this
    override fun addSelectionListener(p0: ISelectionListener?) {}
    override fun activated() {}
    override fun deactivated() {}
    override fun projectOpened(p0: ProjectEvent?) {}
    override fun projectClosed(p0: ProjectEvent?) {}
    override fun projectChanged(p0: ProjectEvent?) {}
}