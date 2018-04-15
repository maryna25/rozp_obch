package main

import (
	"bufio"
	"os"
	"sync"
	"fmt"
	"strings"
	"io"
	"time"
	"math/rand"
)

func main() {
	surnamesFile := openFile("surnames.txt")
	surnamesFileReader := bufio.NewReader(surnamesFile)

	phoneFile := openFile("phones.txt")
	phoneFileReader := bufio.NewReader(phoneFile)

	newDataFile := openFile("new_data.txt")
	newDataFileReader := bufio.NewReader(newDataFile)

	defer surnamesFile.Close()
	defer phoneFile.Close()
	defer newDataFile.Close()

	var mutex = &sync.RWMutex{}

	go surnameReader(mutex, surnamesFileReader)
	go telephoneReader(mutex, phoneFileReader)
	go writer(mutex, newDataFileReader)

	var input string
	fmt.Scanln(&input)
}

func openFile(path string) *os.File {
	file, err := os.Open(path)
	check(err)
	return file
}

func surnameReader(mutex *sync.RWMutex, commandsFileReader *bufio.Reader) {
	for {
		surname := read(commandsFileReader, '\n')
		if surname == "EOF" { break }

		mutex.RLock()
		f, err := os.OpenFile("data.txt", os.O_RDWR|os.O_CREATE, 0755)
		check(err)
		info := findInfoBySurname(f, surname)
		f.Close()
		mutex.RUnlock()

		if info != "" {
			fmt.Println("phone_finder: ", info)
		} else {
			fmt.Println("phone_finder: Not Found! :", surname)
		}
		time.Sleep(time.Second * time.Duration(rand.Intn(8)))
	}
}

func telephoneReader(mutex *sync.RWMutex, commandsFileReader *bufio.Reader) {
	for {
		phone := read(commandsFileReader, '\n')
		if phone == "EOF" { break }

		mutex.RLock()
		f, err := os.OpenFile("data.txt", os.O_RDWR|os.O_CREATE, 0755)
		check(err)
		info := findInfoByPhoneNumber(f, phone)
		f.Close()
		mutex.RUnlock()

		if info != "" {
			fmt.Println("pib finder: ", info)
		} else {
			fmt.Println("pib finder: Not Found :", phone)
		}

		time.Sleep(time.Second * time.Duration(rand.Intn(8)))
	}
}

func writer(mutex *sync.RWMutex, commandsFileReader *bufio.Reader) {
	for {
		time.Sleep(time.Second *time.Duration(rand.Intn(8)))

		whatToDo := rand.Intn(2)

		if whatToDo == 0 {
			data := read(commandsFileReader, '\n')
			if data == "EOF" { break }

			mutex.Lock()
			f, err := os.OpenFile("data.txt", os.O_RDWR | os.O_APPEND | os.O_SYNC, 0666)
			check(err)
			writeDataIntoFile(f, data)
			f.Close()
			mutex.Unlock()

			fmt.Println("writer added: ", data)
		} else {
				mutex.Lock()
				f, err := os.OpenFile("data.txt", os.O_RDWR | os.O_SYNC, 0755)
				check(err)
				n := deleteDataFromFile(f)
				mutex.Unlock()

				fmt.Println("writer removed line number: ", n)
		}
	}
}

func findInfoBySurname(f *os.File, surname string) string {
	f.Seek(0, 0)
	reader := bufio.NewReader(f)

	for {
		curr := read(reader, ' ')
		if curr == "EOF" { break }

		if curr == surname {
			str := read(reader, '\n')
			if str == "" { break }
			return strings.Split(str, "-")[1]
		} else {
			read(reader, '\n')
		}
	}

	return ""
}

func findInfoByPhoneNumber(f *os.File, phoneNumber string) string {
	f.Seek(0, 0)
	reader := bufio.NewReader(f)

	for {
		info := ""
		for i := 0; i < 3; i++ {
			s := read(reader, ' ')
			if s == "EOF" { break }
			info += s + " "
		}

		curr := read(reader, '\n')
		if curr == "EOF" { break }
		if strings.Split(curr, " ")[1] == phoneNumber {
			return info
		}
	}

	return ""
}


func writeDataIntoFile(f *os.File, data string) {
	w:= bufio.NewWriter(f)
	fmt.Fprintln(w, data)
	w.Flush()
}

func deleteDataFromFile(f *os.File) int{
	n := rand.Intn(10)
	r := bufio.NewReader(f)
	str := ""
	count := 0
	for {
		s := read(r, '\n')
		if s == "EOF" {
			break
		}
		if count != n {
			str += s + "\n"
		}
		count++
	}

	f.Close()
	err := os.Remove("data.txt")
	check(err)
	f1, err := os.OpenFile("data.txt", os.O_WRONLY | os.O_CREATE, 0644)
	check(err)
	w := bufio.NewWriter(f1)
	fmt.Fprintln(w, str[:len(str) - 1])
	w.Flush()
	f1.Close()

	return n
}

func check(e error) {
  if e != nil && e != io.EOF {
    panic(e)
  }
}

func read(reader *bufio.Reader, delimiter byte) string {
	str, err := reader.ReadString(delimiter)
	if err == io.EOF {
		return "EOF"
	}
	check(err)
	str = str[:len(str) - 1]
	return str
}
