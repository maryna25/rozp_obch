package main

import (
	"math/rand"
	"time"
	"fmt"
	"sync"
	"strconv"
	"os"
	"bufio"
)

func main() {
	N := 5
	mutex := &sync.RWMutex{}
	garden := make([][]int, N, N)

	for i := 0; i < N; i++ {
		garden[i] = make([]int, N, N)
		for j := 0; j < N; j++ {
			garden[i][j] = rand.Intn(5)
		}
	}

	go gardener(N, garden, mutex)
	go nature(N, garden, mutex)
	go printOnScreen(N, garden, mutex)
	go printToFile(N, garden, mutex)

	var input string
	fmt.Scanln(&input)
}

func gardener(N int, garden [][]int, mutex *sync.RWMutex) {
	for {
		time.Sleep(time.Second * time.Duration(rand.Intn(5)))
		for i := 0; i < N; i++ {
			for j := 0; j < N; j++ {
				mutex.Lock()
				if garden[i][j] != 5 {
					garden[i][j]++
				}
				mutex.Unlock()
			}
		}
	}
}

func nature(N int, garden [][]int, mutex *sync.RWMutex) {
	for {
		time.Sleep(time.Second * time.Duration(rand.Intn(10)))
		for i := 0; i < 3; i++ {
			iPos := rand.Intn(N)
			jPos := rand.Intn(N)

			mutex.Lock()
			garden[iPos][jPos] = rand.Intn(5)
			mutex.Unlock()
		}
	}
}

func printOnScreen(N int, garden [][]int, mutex *sync.RWMutex, ) {
	for {
		mutex.RLock()
		for i := 0; i < N; i++ {
			for j := 0; j < N; j++ {
				fmt.Print(garden[i][j], " ")
			}
			fmt.Println()
		}
		mutex.RUnlock()
		fmt.Println()
		time.Sleep(time.Second * time.Duration(rand.Intn(5)))
	}
}

func printToFile(N int, garden [][]int, mutex *sync.RWMutex, ) {
	for {
		mutex.RLock()
		s := ""
		for i := 0; i < N; i++ {
			for j := 0; j < N; j++ {
				s += strconv.Itoa(garden[i][j]) + " "
			}
			s += "\n"
		}
		f, err := os.OpenFile("garden.log", os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
		check(err)
		writeDataIntoFile(f, s)
		mutex.RUnlock()
		time.Sleep(time.Second * time.Duration(rand.Intn(5)))
	}
}

func check(e error) {
  if e != nil {
    panic(e)
  }
}

func writeDataIntoFile(f *os.File, data string)  {
	w:= bufio.NewWriter(f)
	fmt.Fprintln(w, data)
	w.Flush()
}
