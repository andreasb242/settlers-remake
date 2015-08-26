/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.graphics.map.controls.original.panel.content;

import go.graphics.GLDrawContext;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.ui.UIPanel;

/**
 * This is a bar that is filled
 * 
 * @author michael
 *
 */
public class BarFill extends UIPanel {

	private static final ImageLink barImageLink = new OriginalImageLink(EImageLinkType.GUI, 3, 336, 0); // checked in the original game

	private static final float EMPTY_X = .1f;
	private static final float FULL_X = .9f;

	private final UIPanel fill;
	private ExecutableAction listener;
	private float value = 0;

	public BarFill() {
		fill = new UIPanel() {
			@Override
			public void drawAt(GLDrawContext gl) {
				gl.color(0f, 0.78f, 0.78f, 1f);
				FloatRectangle position = getPosition();
				gl.fillQuad(position.getMinX(), position.getMinY(), position.getMaxX(), position.getMaxY());
			}
		};

		setBackground(barImageLink);
		setBarFill(value);
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		gl.color(0f, 0.78f, 0.78f, 1f);
		FloatRectangle position = getPosition();
		float fillx = value < .01f ? 0 : value > .99f ? 1 : EMPTY_X * (1 - value) + FULL_X * value;
		float maxx = position.getMinX() * (1 - fillx) + position.getMaxX() * fillx;
		gl.fillQuad(position.getMinX(), position.getMinY(), maxx, position.getMaxY());

		super.drawBackground(gl);
	}

	@Override
	public Action getAction(final float relativex, float relativey) {
		final float relativeFill = getFillForClick(relativex);
		return new ExecutableAction() {
			@Override
			public void execute() {
				setBarFill(relativeFill);
				if (listener != null) {
					listener.execute();
				}
			}
		};
	}

	private float getFillForClick(final float relativex) {
		if (relativex < EMPTY_X) {
			return 0;
		} else if (relativex > FULL_X) {
			return 1;
		} else {
			return (relativex - EMPTY_X) / (FULL_X - EMPTY_X);
		}
	}

	@Override
	public String getDescription(float relativex, float relativey) {
		return Math.round(value * 100) + "%";
	}

	public void setAction(ExecutableAction action) {
		this.listener = action;
	}

	/**
	 *
	 * @return A value from 0 to 1 indicating the proportion of the bar filled.
	 */
	public float getValue() {
		return value;
	}

	/**
	 *
	 * @param percentage
	 *            expects a value in the range 0.0 to 1.0. Values outside this range will be clamped.
	 */
	public void setBarFill(float percentage) {
		if (percentage < 0) {
			percentage = 0;
		}
		else if (percentage > 1) {
			percentage = 1;
		}
		value = percentage;
		removeAll();
		addChild(fill, 0f, 0f, percentage, 1f);
	}
}