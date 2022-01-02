(ns clojure-lge.simple
  (:require [clojure.pprint :as pprint])
  (:import (java.nio ByteBuffer FloatBuffer)
           (org.lwjgl BufferUtils)
           (org.lwjgl.opengl GL GL11 GL15 GL20 GL30)
           (org.lwjgl.glfw GLFW GLFWErrorCallback GLFWKeyCallback)
           (com.boc_dev.maths.objects.vector Vec2f)))

(declare main-loop)

(defn update-boid [boid all-boids]
  (Vec2f. (.getX boid) (- (.getY boid) 0.001)))

(defn iterate-boids [boid-data]
  (mapv (fn [boid] (update-boid boid boid-data)) boid-data))

(defn draw [window boid-data]

  (GLFW/glfwPollEvents)

  (GL11/glClear GL11/GL_COLOR_BUFFER_BIT)

  (GL11/glBegin GL11/GL_POINTS)

  (GL11/glColor4f 0.9725 0.9725 0.949 1)

  (doseq [boid-vec boid-data] (GL11/glVertex2f (.getX boid-vec) (.getY boid-vec)))

  (GL11/glEnd)

  (GLFW/glfwSwapBuffers window)
  
  (main-loop window (iterate-boids boid-data)))

(defn main-loop [window boid-data]
  (if (not (GLFW/glfwWindowShouldClose window))
    (draw window boid-data)
    (GLFW/glfwTerminate)))

(defn create-window [width height title boid-data]

  (when-not (GLFW/glfwInit)
    (throw (RuntimeException. "GLFW did not start correctly")))

  (let
   [window (GLFW/glfwCreateWindow width height title 0 0)]

    (GLFW/glfwWindowHint GLFW/GLFW_VISIBLE               GLFW/GLFW_FALSE)

    (when (= window nil)
      (throw (RuntimeException. "Failed to create the GLFW window")))

    (let [videomode (GLFW/glfwGetVideoMode (GLFW/glfwGetPrimaryMonitor))]
      (GLFW/glfwSetWindowPos window
                             (/ (- (.width videomode) width) 2)
                             (/ (- (.height videomode) height) 2))

      (GLFW/glfwShowWindow window))

    (GLFW/glfwMakeContextCurrent window)

    (GL/createCapabilities)

    (GL11/glDisable GL11/GL_BLEND);

    (GL11/glClearColor 0.1569 0.1647 0.2118 1.0)

    (GL11/glPointSize 2)

    (main-loop window boid-data)))

(defn init-boids [n]
  (let [stop n]
    (loop [i 0
           acc []]
      (if (= i stop)
        acc
        (recur (inc i) (conj acc (Vec2f. (- (rand 2) 1)  (- (rand 2) 1))))))))

(defn main
  []
  (println "Running")
  (create-window 800 800 "Sim" (iterate-boids (init-boids 1000))))
