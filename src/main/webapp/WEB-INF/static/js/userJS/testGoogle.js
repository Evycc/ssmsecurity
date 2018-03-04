
      window.requestAnimFrame = (function(callback) {
        return window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || window.oRequestAnimationFrame || window.msRequestAnimationFrame ||
        function(callback) {
          window.setTimeout(callback, 1000 / 60);
        };
      })();

      function initBalls() {
        balls = [];

        var blue = '#3A5BCD';
        var red = '#EF2B36';
        var yellow = '#FFC636';
        var green = '#02A817';

        //J
        balls.push(new Ball(173, 50, 0, 0, blue));
        balls.push(new Ball(158, 50, 0, 0, blue));
        balls.push(new Ball(144, 50, 0, 0, blue));
        balls.push(new Ball(130, 50, 0, 0, blue));
        balls.push(new Ball(117, 50, 0, 0, blue));
        balls.push(new Ball(103, 50, 0, 0, blue));
        balls.push(new Ball(140, 62, 0, 0, blue));
        balls.push(new Ball(140, 78, 0, 0, blue));
        balls.push(new Ball(142, 94, 0, 0, blue));
        balls.push(new Ball(141, 110, 0, 0, blue));
        balls.push(new Ball(141, 124, 0, 0, blue));
        balls.push(new Ball(141, 138, 0, 0, blue));
        balls.push(new Ball(136, 151, 0, 0, blue));
        balls.push(new Ball(128, 159, 0, 0, blue));
        balls.push(new Ball(117, 158, 0, 0, blue));
        balls.push(new Ball(105, 148, 0, 0, blue));
        balls.push(new Ball(98, 138, 0, 0, blue));

        //I
        balls.push(new Ball(258, 56, 0, 0, red));
        balls.push(new Ball(240, 56, 0, 0, red));
        balls.push(new Ball(224, 56, 0, 0, red));
        balls.push(new Ball(210, 56, 0, 0, red));
        balls.push(new Ball(232, 71, 0, 0, red));
        balls.push(new Ball(232, 88, 0, 0, red));
        balls.push(new Ball(232, 104, 0, 0, red));
        balls.push(new Ball(232, 120, 0, 0, red));
        balls.push(new Ball(258, 136, 0, 0, red));
        balls.push(new Ball(240, 136, 0, 0, red));
        balls.push(new Ball(224, 136, 0, 0, red));
        balls.push(new Ball(210, 136, 0, 0, red));

        //N
        balls.push(new Ball(295,49, 0, 0, green));
        balls.push(new Ball(294,64, 0, 0, green));
        balls.push(new Ball(294,80, 0, 0, green));
        balls.push(new Ball(296,94, 0, 0, green));
        balls.push(new Ball(294,109, 0, 0, green));
        balls.push(new Ball(294,123, 0, 0, green));
        balls.push(new Ball(294,139, 0, 0, green));
        balls.push(new Ball(305,58, 0, 0, green));
        balls.push(new Ball(315,68, 0, 0, green));
        balls.push(new Ball(324,79, 0, 0, green));
        balls.push(new Ball(330,89, 0, 0, green));
        balls.push(new Ball(338,100, 0, 0, green));
        balls.push(new Ball(347,112, 0, 0, green));
        balls.push(new Ball(353,123, 0, 0, green));
        balls.push(new Ball(359,135, 0, 0, green));
        balls.push(new Ball(359,120, 0, 0, green));
        balls.push(new Ball(359,106, 0, 0, green));
        balls.push(new Ball(359,91, 0, 0, green));
        balls.push(new Ball(359,78, 0, 0, green));
        balls.push(new Ball(359,65, 0, 0, green));
        balls.push(new Ball(359,50, 0, 0, green));

        //G
        balls.push(new Ball(473, 63, 0, 0, yellow));
        balls.push(new Ball(458, 53, 0, 0, yellow));
        balls.push(new Ball(443, 52, 0, 0, yellow));
        balls.push(new Ball(430, 53, 0, 0, yellow));
        balls.push(new Ball(417, 58, 0, 0, yellow));
        balls.push(new Ball(410, 70, 0, 0, yellow));
        balls.push(new Ball(402, 82, 0, 0, yellow));
        balls.push(new Ball(404, 96, 0, 0, yellow));
        balls.push(new Ball(405, 107, 0, 0, yellow));
        balls.push(new Ball(410, 120, 0, 0, yellow));
        balls.push(new Ball(424, 130, 0, 0, yellow));
        balls.push(new Ball(439, 136, 0, 0, yellow));
        balls.push(new Ball(452, 136, 0, 0, yellow));
        balls.push(new Ball(466, 136, 0, 0, yellow));
        balls.push(new Ball(474, 127, 0, 0, yellow));
        balls.push(new Ball(479, 110, 0, 0, yellow));
        balls.push(new Ball(466, 109, 0, 0, yellow));
        balls.push(new Ball(456, 110, 0, 0, yellow));

        return balls;
      }
      function getMousePos(canvas, evt) {
        // get canvas position
        var obj = canvas;
        var top = 0;
        var left = 0;
        while(obj.tagName != 'BODY') {
          top += obj.offsetTop;
          left += obj.offsetLeft;
          obj = obj.offsetParent;
        }

        // return relative mouse position
        var mouseX = evt.clientX - left + window.pageXOffset;
        var mouseY = evt.clientY - top + window.pageYOffset;
        return {
          x: mouseX,
          y: mouseY
        };
      }
      function updateBalls(canvas, balls, timeDiff, mousePos) {
        var context = canvas.getContext('2d');
        var collisionDamper = 0.3;
        var floorFriction = 0.0005 * timeDiff;
        var mouseForceMultiplier = 1 * timeDiff;
        var restoreForce = 0.002 * timeDiff;

        for(var n = 0; n < balls.length; n++) {
          var ball = balls[n];
          // set ball position based on velocity
          ball.y += ball.vy;
          ball.x += ball.vx;

          // restore forces
          if(ball.x > ball.origX) {
            ball.vx -= restoreForce;
          }
          else {
            ball.vx += restoreForce;
          }
          if(ball.y > ball.origY) {
            ball.vy -= restoreForce;
          }
          else {
            ball.vy += restoreForce;
          }

          // mouse forces
          var mouseX = mousePos.x;
          var mouseY = mousePos.y;

          var distX = ball.x - mouseX;
          var distY = ball.y - mouseY;

          var radius = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));

          var totalDist = Math.abs(distX) + Math.abs(distY);

          var forceX = (Math.abs(distX) / totalDist) * (1 / radius) * mouseForceMultiplier;
          var forceY = (Math.abs(distY) / totalDist) * (1 / radius) * mouseForceMultiplier;

          if(distX > 0) {// mouse is left of ball
            ball.vx += forceX;
          }
          else {
            ball.vx -= forceX;
          }
          if(distY > 0) {// mouse is on top of ball
            ball.vy += forceY;
          }
          else {
            ball.vy -= forceY;
          }

          // floor friction
          if(ball.vx > 0) {
            ball.vx -= floorFriction;
          }
          else if(ball.vx < 0) {
            ball.vx += floorFriction;
          }
          if(ball.vy > 0) {
            ball.vy -= floorFriction;
          }
          else if(ball.vy < 0) {
            ball.vy += floorFriction;
          }

          // floor condition
          if(ball.y > (canvas.height - ball.radius)) {
            ball.y = canvas.height - ball.radius - 2;
            ball.vy *= -1;
            ball.vy *= (1 - collisionDamper);
          }

          // ceiling condition
          if(ball.y < (ball.radius)) {
            ball.y = ball.radius + 2;
            ball.vy *= -1;
            ball.vy *= (1 - collisionDamper);
          }

          // right wall condition
          if(ball.x > (canvas.width - ball.radius)) {
            ball.x = canvas.width - ball.radius - 2;
            ball.vx *= -1;
            ball.vx *= (1 - collisionDamper);
          }

          // left wall condition
          if(ball.x < (ball.radius)) {
            ball.x = ball.radius + 2;
            ball.vx *= -1;
            ball.vx *= (1 - collisionDamper);
          }
        }
      }
      function Ball(x, y, vx, vy, color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.color = color;
        this.origX = x;
        this.origY = y;
        this.radius = 10;
      }
      function animate(canvas, balls, lastTime, mousePos) {
        var context = canvas.getContext('2d');

        // update
        var date = new Date();
        var time = date.getTime();
        var timeDiff = time - lastTime;
        updateBalls(canvas, balls, timeDiff, mousePos);
        lastTime = time;

        // clear
        context.clearRect(0, 0, canvas.width, canvas.height);

        // render
        for(var n = 0; n < balls.length; n++) {
          var ball = balls[n];
          context.beginPath();
          context.arc(ball.x, ball.y, ball.radius, 0, 2 * Math.PI, false);
          context.fillStyle = ball.color;
          context.fill();
        }

        // request new frame
        requestAnimFrame(function() {
          animate(canvas, balls, lastTime, mousePos);
        });
      }
      var canvas = document.getElementById('myCanvas');
      var balls = initBalls();
      var date = new Date();
      var time = date.getTime();
      /*
       * set mouse position really far away
       * so the mouse forces are nearly obsolete
       */
      var mousePos = {
        x: 9999,
        y: 9999
      };

      canvas.addEventListener('mousemove', function(evt) {
        var pos = getMousePos(canvas, evt);
        mousePos.x = pos.x;
        mousePos.y = pos.y;
      });

      canvas.addEventListener('mouseout', function(evt) {
        mousePos.x = 9999;
        mousePos.y = 9999;
      });
      animate(canvas, balls, time, mousePos);
