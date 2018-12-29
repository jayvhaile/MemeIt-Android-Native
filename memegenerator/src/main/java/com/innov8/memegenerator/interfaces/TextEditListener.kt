package com.innov8.memegenerator.interfaces

import com.innov8.memegenerator.MemeEngine.MemeLayout
import com.innov8.memegenerator.MemeEngine.MemeStickerView
import com.innov8.memegenerator.MemeEngine.MemeTextView
import com.innov8.memegenerator.MemeEngine.PaintHandler
import com.memeit.backend.models.MemeTextStyleProperty

interface TextEditListener {
    fun onAddText(memeTextView: MemeTextView)
    fun onTextSizeChanged(size: Float)
    fun onTextColorChanged(color: Int)
    fun onTextFontChanged(font: String)
    fun onTextSetBold(bold: Boolean)
    fun onTextSetItalic(italic: Boolean)
    fun onTextSetAllCap(allCap: Boolean)
    fun onTextSetStroked(stroked: Boolean)
    fun onTextStrokeChanged(strokeSize: Float)
    fun onTextStrokrColorChanged(strokeColor: Int)
    fun onApplyAll(textStyleProperty: MemeTextStyleProperty, applySize: Boolean = true)
}

interface LayoutEditInterface {
    fun onLayoutSet(memeLayout: MemeLayout)
    fun onAllMarginSet(left: Int, top: Int = left, right: Int = left, bottom: Int = left)
    fun onLeftMargin(size: Int)
    fun onRightMargin(size: Int)
    fun onTopMargin(size: Int)
    fun onBottomMargin(size: Int)
    fun onVertivalSpacing(size: Int)
    fun onHorizontalSpacing(size: Int)
    fun onBackgroundColorChanged(color: Int)
}

interface StickerEditInterface {
    fun onAddSticker(memeStickerView: MemeStickerView)
}

interface PaintEditInterface {
    fun onBrushSizeChanged(size: Float)
    fun onBrushColorChanged(color: Int)
    fun onShapeChanged(paintMode: PaintHandler.PaintMode)
    fun onPaintUndo()
    fun hasUndo(): Boolean
}