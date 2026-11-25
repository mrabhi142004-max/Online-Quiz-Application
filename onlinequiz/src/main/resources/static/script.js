
let quizQuestions = [];


const categorySelect =
    document.getElementById("categorySelect");

const loadCategoryBtn =
    document.getElementById("loadCategoryBtn");

let selectedCategory = "Java";


const questionContainer =
    document.getElementById("questions");

const quizForm =
    document.getElementById("quizForm");

const statusText =
    document.getElementById("status");

const resultDiv =
    document.getElementById("result");

const submitBtn =
    document.getElementById("submitBtn");

const restartBtn =
    document.getElementById("restartBtn");

// Load questions from Spring Boot
async function loadQuestions() {

    submitBtn.disabled = true;
    statusText.textContent = "Loading questions...";

    try {
        const response = await fetch(
    `/quiz/questions?category=${encodeURIComponent(selectedCategory)}`
);

        if (!response.ok) {
            throw new Error("Failed to load questions");
        }

        quizQuestions = await response.json();

        questionContainer.innerHTML = "";

        if (quizQuestions.length === 0) {
            statusText.textContent =
                "No questions available.";
            return;
        }

        statusText.textContent =
            `Total Questions: ${quizQuestions.length}`;

        quizQuestions.forEach((question, index) => {

            const card = document.createElement("div");
            card.className = "question-card";

            const heading = document.createElement("h3");
            heading.textContent =
                `${index + 1}. ${question.questionTitle}`;

            card.appendChild(heading);

            const options = [
                question.option1,
                question.option2,
                question.option3,
                question.option4
            ];

            options.forEach(option => {

                const label = document.createElement("label");
                label.className = "option";

                const radio = document.createElement("input");
                radio.type = "radio";
                radio.name = `question_${question.id}`;
                radio.value = option;

                label.appendChild(radio);
                label.appendChild(
                    document.createTextNode(option)
                );

                card.appendChild(label);
            });

            questionContainer.appendChild(card);
        });

        submitBtn.disabled = false;

    } catch (error) {
        statusText.textContent =
            "Error loading quiz. Check the server.";
        console.error(error);
    }
}

// Submit selected answers
quizForm.addEventListener("submit", async function(event) {

    event.preventDefault();

    const answers = [];

    for (const question of quizQuestions) {

        const selected = document.querySelector(
            `input[name="question_${question.id}"]:checked`
        );

        if (!selected) {
            alert("Please answer all questions.");
            return;
        }

        answers.push({
            questionId: question.id,
            selectedAnswer: selected.value
        });
    }

    submitBtn.disabled = true;
    statusText.textContent = "Submitting quiz...";

    try {
        const response = await fetch(
    `/quiz/submit?category=${encodeURIComponent(selectedCategory)}`,
    {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(answers)
        });

        
if (!response.ok) {
    const errorText = await response.text();

    console.error("HTTP Status:", response.status);
    console.error("Backend Error:", errorText);
    console.log("Submitted Answers:", answers);

    throw new Error(
        "Submission failed: HTTP " + response.status
    );
}


        const result = await response.json();

        resultDiv.textContent =
            `Score: ${result.correctAnswers}/${result.totalQuestions} ` +
            `(${result.percentage.toFixed(2)}%)`;

        statusText.textContent = "Quiz completed!";

        quizForm.style.display = "none";
        restartBtn.style.display = "block";

    } catch (error) {
        statusText.textContent =
            "Submission failed. Please try again.";
        submitBtn.disabled = false;
        console.error(error);
    }
});

// Restart the quiz
function restartQuiz() {

    resultDiv.textContent = "";
    restartBtn.style.display = "none";
    quizForm.style.display = "block";

    loadQuestions();
}


loadCategoryBtn.addEventListener("click", async () => {

    selectedCategory = categorySelect.value;

    resultDiv.textContent = "";

    await loadQuestions();
});


// Load quiz when page opens
loadQuestions();
