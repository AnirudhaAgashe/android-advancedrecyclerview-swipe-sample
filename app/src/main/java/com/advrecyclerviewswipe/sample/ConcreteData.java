/*
 *    Copyright (C) 2015
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.advrecyclerviewswipe.sample;

public final class ConcreteData {

    private final long mId;
    private final String mText;
    private final int mViewType;
    private boolean mPinned;

    ConcreteData(long id, int viewType, String text, int swipeReaction) {
        mId = id;
        mViewType = viewType;
        mText = makeText(id, text, swipeReaction);
    }

    private static String makeText(long id, String text, int swipeReaction) {
        final StringBuilder sb = new StringBuilder();

        sb.append(id);
        sb.append(" - ");
        sb.append(text);

        return sb.toString();
    }

    public boolean isSectionHeader() {
        return false;
    }

    public int getViewType() {
        return mViewType;
    }

    public long getId() {
        return mId;
    }

    public String toString() {
        return mText;
    }

    public String getText() {
        return mText;
    }

    public boolean isPinned() {
        return mPinned;
    }

    public void setPinned(boolean pinned) {
        mPinned = pinned;
    }
}
