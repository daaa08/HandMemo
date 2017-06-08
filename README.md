## onTouchEvent
- View클래스에서 오버라이드하여 사용 가능
- onTouchEvent(MotionEvent event)의 MotionEvent는 터치와 관련된 정보를 가져 옴

```java
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
```
