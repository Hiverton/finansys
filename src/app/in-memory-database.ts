import { InMemoryDbService } from 'angular-in-memory-web-api'
import { Category } from './pages/categories/shared/category.model';

export class InMemoryDataBase implements InMemoryDbService {

    createDb(){
        const categories: Category[] = [
            {id: 1, name: "Maquinário", description: ""},
            {id: 2, name: "Matéria-prima", description: ""}
        ];

        const entries: any[] = [
            {id: 1, name: "Trator", categoryId: categories[0].id, category: categories[0], paid: false, amount: "70,00", date: "10/10/2019", type: "expense"},
            {id: 2, name: "Caminhão A", categoryId: categories[0].id, category: categories[0], paid: false, amount: "900,00", date: "01/10/2019", type: "expense"},
            {id: 3, name: "Minério de ferro", categoryId: categories[1].id, category: categories[1], paid: true, amount: "2000,00", date: "05/01/2019", type: "renevue"},
         ]

        return { categories, entries };
    }
}