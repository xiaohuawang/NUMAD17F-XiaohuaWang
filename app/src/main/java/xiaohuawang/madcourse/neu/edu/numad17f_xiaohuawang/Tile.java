/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband4 for more book information.
 ***/
package xiaohuawang.madcourse.neu.edu.numad17f_xiaohuawang;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.util.AttributeSet;
import android.view.View;



public class Tile {

    private GameFragment mGame;
    private View mView;
    private Tile mSubTiles[];

    public Tile(GameFragment game) {
        this.mGame = game;
    }

    public Tile(GameFragment game, AttributeSet paramAttributeSet) {

    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        this.mView = view;
    }


    public Tile[] getSubTiles() {
        return mSubTiles;
    }

    public void animate() {

        Animator anim = AnimatorInflater.loadAnimator(mGame.getActivity(),
                R.animator.tictactoe);
        if (getView() != null) {
            anim.setTarget(getView());
            anim.start();
        }
    }
}
