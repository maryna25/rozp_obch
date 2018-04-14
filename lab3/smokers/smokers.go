package main

import (
	"math/rand"
	"fmt"
	"time"
)

// tobacco = 0 paper = 1 matches = 2

func main() {
	var smoker_channels [3]chan string
	for i := 0; i < 3; i++ {
		smoker_channels[i]= make(chan string)
	}
	barman_chanel := make(chan [2]string)

	go smoker("tobacco", smoker_channels[0], barman_chanel)
	go smoker("paper", smoker_channels[1], barman_chanel)
	go smoker("matches", smoker_channels[2], barman_chanel)

	go barman(barman_chanel, smoker_channels)

	var input string
	fmt.Scanln(&input)
}

func generateNumbers()(x, y int) {
	x = rand.Intn(3)
	y = rand.Intn(3)
	for x == y {
		y = rand.Intn(3)
	}
	return x, y
}

func barman(barman_chanel chan [2]string, smoker_channels [3]chan string) {
	for {
		first, second := generateNumbers()
		firstComponent := <- smoker_channels[first]
		secondComponent := <- smoker_channels[second]
		array :=[2]string{firstComponent, secondComponent}
		// Give ingridients
		barman_chanel <- array

		val := <- barman_chanel
		for val[0] != "Done" {
			//Show third smoker that he can take ingridients
			// fmt.Println(3 - first - second)
			<- smoker_channels[3 - first - second]
			// Give ingridients
			barman_chanel <- val
			//Wait till smoker finishes
			val = <- barman_chanel
		}
		fmt.Println("Barman:", val[0])
		time.Sleep(time.Second * 2)
	}
}

func smoker(name_of_component string, channel chan string, barman_chanel chan [2]string) {
	for {
		//each smoker propose his ingridients
		channel <- name_of_component
		//see barman's ingridients
		new_component := <- barman_chanel
		fmt.Println(name_of_component, new_component)

		//see if this smoker can use it
		if (new_component[0] != name_of_component) && (new_component[1] != name_of_component) {
			fmt.Println("Smoker with " + name_of_component + " has done a cigarette")
			barman_chanel <- [2]string {"Done", ""}
		} else {
			//do not match, give it back
			barman_chanel <- new_component
		}
		time.Sleep(time.Second * 2)
	}

}
