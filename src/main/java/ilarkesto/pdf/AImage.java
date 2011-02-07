package ilarkesto.pdf;

import java.io.File;

public abstract class AImage extends AParagraphElement {

	public enum Align {
		LEFT, RIGHT
	}

	protected byte[] data;
	protected File file;
	protected Float scaleByHeight;
	protected Float scaleByWidth;
	protected Align align;
	protected float marginTop = 0f;
	protected float marginRight = 0f;
	protected float marginBottom = 0f;
	protected float marginLeft = 0f;

	public AImage(APdfElement parent, byte[] data) {
		super(parent);
		this.data = data;
	}

	public AImage(APdfElement parent, File file) {
		super(parent);
		this.file = file;
	}

	public Align getAlign() {
		return align;
	}

	// --- helper ---

	public AImage setAlignLeft() {
		return setAlign(Align.LEFT);
	}

	public AImage setAlignRight() {
		return setAlign(Align.RIGHT);
	}

	public AImage setMargin(float top, float right, float bottom, float left) {
		setMarginTop(top);
		setMarginRight(right);
		setMarginBottom(bottom);
		setMarginLeft(left);
		return this;
	}

	public AImage setMargin(float topBottom, float leftRight) {
		return setMargin(topBottom, leftRight, topBottom, leftRight);
	}

	public AImage setMargin(float margin) {
		setMarginTop(margin);
		setMarginRight(margin);
		setMarginBottom(margin);
		setMarginLeft(margin);
		return this;
	}

	// --- dependencies ---

	public AImage setMarginTop(float marginTop) {
		this.marginTop = marginTop;
		return this;
	}

	public AImage setMarginRight(float marginRight) {
		this.marginRight = marginRight;
		return this;
	}

	public AImage setMarginBottom(float marginBottom) {
		this.marginBottom = marginBottom;
		return this;
	}

	public AImage setMarginLeft(float marginLeft) {
		this.marginLeft = marginLeft;
		return this;
	}

	public AImage setScaleByHeight(Float scaleByHeight) {
		this.scaleByHeight = scaleByHeight;
		return this;
	}

	public AImage setScaleByWidth(Float scaleByWidth) {
		this.scaleByWidth = scaleByWidth;
		return this;
	}

	public AImage setAlign(Align align) {
		this.align = align;
		return this;
	}

}
