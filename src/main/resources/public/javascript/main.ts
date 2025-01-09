import { GoogleBookFinder } from './GoogleBookFinder';
import { GoogleSheetsService } from './GoogleSheetsService';

const userId = localStorage.getItem('user_id');
const idToken = localStorage.getItem('id_token');

if (userId && idToken) {
    GoogleSheetsService.fetchSheetId(userId)
        .then(sheetId => {
            if (!sheetId) {
                GoogleSheetsService.createSheet(userId)
                    .then(newSheetId => {
                        console.log('New sheet created:', newSheetId);
                    })
                    .catch(error => {
                        console.error('Error creating new sheet:', error);
                    });
            } else {
                console.log('Sheet ID:', sheetId);
            }
        })
        .catch(error => {
            console.error('Error fetching sheet ID:', error);
        });

    const isbnInput = document.getElementById('isbnInput') as HTMLInputElement;
    const searchButton = document.getElementById('searchButton') as HTMLButtonElement;

    searchButton.addEventListener('click', () => {
        const isbn = isbnInput.value;
        GoogleBookFinder.findByIsbn(isbn, idToken)
            .then(bookData => {
                if (bookData) {
                    console.log('Book found:', bookData);
                    GoogleSheetsService.insertBook(userId, bookData)
                        .then(() => {
                            console.log('Book inserted into Google Sheets');
                        })
                        .catch(error => {
                            console.error('Error inserting book into Google Sheets:', error);
                        });
                } else {
                    console.log('No book found for ISBN:', isbn);
                }
            })
            .catch(error => {
                console.error('Error finding book:', error);
            });
    });
} else {
    console.error('User not authenticated');
}
