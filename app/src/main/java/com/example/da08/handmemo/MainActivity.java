package com.example.da08.handmemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    FrameLayout layout;
    SeekBar seekBar;
    RadioGroup colorGroup;

    float c_seekBar = 10f; // 기본 브러쉬 두께 값 세팅

    Board board;

    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        layout = (FrameLayout) findViewById(R.id.layout);

        colorGroup = (RadioGroup) findViewById(R.id.colorGroup);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        imageView = (ImageView)findViewById(R.id.imageView);


        colorGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.Button1:
                        setBrush(Color.BLUE);
                        break;
                    case R.id.Button2:
                        setBrush(Color.RED);
                        break;
                    case R.id.Button3:
                        setBrush(Color.CYAN);
                        break;
                }
            }
        });

        // seekbar 처리
        seekBar.setProgress(10);  // progress의 처음 시작
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                c_seekBar = i + 1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 드로잉 캐시를 지워줌
                view.destroyDrawingCache();
                // 다시 만들고
                layout.buildDrawingCache();
                // 레이아웃의 그려진 내용을 Bitmap 형태로 가져온다.
                Bitmap capture = layout.getDrawingCache();
                // 캡쳐한 이미지를 썸네일에 보여준다.
                imageView.setImageBitmap(capture);
            }
        });

        /*
           기본적인 View 세팅
         */
        // 1 보드를 새로 생성
        board = new Board(this);

        // 2 생성된 보드를 화면에 세팅
        layout.addView(board);

        // 기본 페인트값 설정
        setBrush(Color.BLACK);

    }

    private void setBrush(int brushColor) {

        Paint paint = new Paint();
        paint.setColor(brushColor);  // 색상 받아 옴

        board.setPaint(paint);
    }

    /*
     Brush클래스를 만든 이유
     : 만들어주지 않으면 이미 그려둔 그림들까지 싹 다 바뀌므로
     하나의 객체로 해줘야 함
     */

    class Brush {
        Paint paint;
        Path path;
        float seekBar;
    }

    // 그림이 그려지는 곳
    class Board extends View {
        List<Brush> brushes = new ArrayList<>();
        Paint paint;
        Path c_path;  // 현재 터치한 포인트들에 점을찍어 선을 이어줌

        public Board(Context context) {
            super(context);
        }



        public void setPaint(Paint paint){
            this.paint = paint;
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);  // 선을 부드럽게 그려 줌
            paint.setStrokeJoin(Paint.Join.ROUND);  // 끊긴 부분을 이어줌
            paint.setStrokeCap(Paint.Cap.ROUND);  // 끊긴 부분을 닫아줌
            paint.setDither(true); // 선 사이의 노이즈?같은걸 부드럽게 잡아 줌 (노이즈제거)
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for (Brush brush : brushes) {

                canvas.drawPath(brush.path, brush.paint);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX(); // 내가 터치한 좌표를 꺼낸다
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 새로운 붓 생성
                    Brush brush = new Brush();
                    // 새로운 패스 생성
                    c_path = new Path();
                    // 생성한 붓과 패스를 브러쉬에 담음
                    brush.paint = paint;
                    brush.path = c_path;
                    brush.paint.setStrokeWidth(c_seekBar);
                    Log.d("TAG","c_seekBar==========================="+c_seekBar);
                    // 완성된 브러쉬를 실제 그릴 수 있도록
                    brushes.add(brush);

                    c_path.moveTo(x, y); // 손을 뗏을경우 그리지 않고 이동
                    break;

                case MotionEvent.ACTION_MOVE:
                    c_path.lineTo(x, y); // 점을찍고 손을 뗴지않고 움직이면 선을 이어 줌

                    break;
                case MotionEvent.ACTION_UP:  // 더이상 그리지 않음
                    c_path.lineTo(x, y);  // 손을 뗀 좌표까진 그려줘야 함
                    break;
            }

            invalidate(); // 화면 갱신해줘야 그림이 보임

            return true;  // 리턴이 false가되면 터치 이벤트를 연속해서 발생 하지 않음. 즉 드래그시 onTouchEvent가 호출되지 않음
        }
    }
}
